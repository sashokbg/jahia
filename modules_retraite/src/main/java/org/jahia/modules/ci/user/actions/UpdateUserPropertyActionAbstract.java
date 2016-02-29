package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author balexandrov
 * @description : update user properties
 */
public abstract class UpdateUserPropertyActionAbstract extends Action implements
		CiConstants {

	/**
	 * 
	 * @param renderContext
	 * @param user
	 *            - the moderator/professional
	 * @param userName
	 *            - user to be updated (not needed for moderators impl)
	 * @return
	 */
	protected abstract boolean canModify(RenderContext renderContext,
			JahiaUser user, String userName);

	@Override
	public ActionResult doExecute(HttpServletRequest req,
			RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters,
			URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();

		if (canModify(renderContext, user,
				getParameter(parameters, "userName", null))) {

			JSONObject json = new JSONObject();
			String userName = getParameter(parameters, "userName", null);
			String propertyKey = getParameter(parameters, "propertyKey", null);
			String propertyValue = getParameter(parameters, "propertyValue",
					null);

			if (Validator.isNotEmpty(userName)
					&& Validator.isNotEmpty(propertyKey)) {
				if (session.isLive())
					session = JCRTemplate
							.getInstance()
							.getSessionFactory()
							.getCurrentUserSession("default",
									session.getLocale(),
									session.getFallbackLocale());
				JahiaUser userToUpdate = userManagerService
						.lookupUser(userName);
				if (userToUpdate != null) {
					if (PROPERTIES_USER_PICTURE.equals(propertyKey)) {
						JCRUser jcrUser = ((JCRUser) userToUpdate);
						JCRSessionFactory.getInstance().setCurrentUser(jcrUser);
						JCRSessionWrapper userToUpdateSession = JCRSessionFactory
								.getInstance().getCurrentUserSession();
						if (UserHelper.deleteCurrentUserAvatar(jcrUser
								.getNode(userToUpdateSession))) {
							json.put("update", true);
							if (_log.isInfoEnabled())
								_log.info("Deleted avatar from moderator view on user : "
										+ userToUpdate);
							userManagerService.updateCache(userToUpdate);
						} else {
							json.put("update", false);
						}
					} else if (!StringUtils.equals(
							userToUpdate.getProperty(propertyKey),
							propertyValue)) {

						// SIGNATURE is richtext, so we don't need xml escape
						if (propertyKey.equals(PROPERTIES_USER_SIGNATURE)) {
							userToUpdate
									.setProperty(propertyKey, propertyValue);

						} else {
							userToUpdate.setProperty(propertyKey,
									Functions.escapeXml(propertyValue));
						}
						session.save();
						json.put("update", "ok");
					}
				}
			}
			return new ActionResult(HttpServletResponse.SC_OK, null, json);
		} else {

			return new ActionResult(HttpServletResponse.SC_FORBIDDEN, null);
		}
	}

	/**
	 * JahiaUserManagerService
	 */
	protected JahiaUserManagerService userManagerService;

	/**
	 * Bean Property setter
	 * 
	 * @param userManagerService
	 */
	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	protected transient static Logger _log = LoggerFactory
			.getLogger(UpdateUserPropertyActionAbstract.class);
}
