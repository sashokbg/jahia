package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRCallback;
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
 * @author el-aarko
 * @description : ...
 */
public class SwitchUserClassAction extends Action implements CiConstants {

	@Override
	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		return (ActionResult) JCRTemplate.getInstance().doExecuteWithSystemSession(getCurrentUser().getName(), "default", new JCRCallback<Object>() {
			public ActionResult doInJCR(JCRSessionWrapper session) throws RepositoryException {
				String userName = getParameter(parameters, "name", "");
				String targetClass = getParameter(parameters, "targetClass", "");

				JahiaUser currentUser = session.getUser();
				JahiaUser user = null;

				JSONObject json = new JSONObject();
				try {
					if (UserHelper.isModerator(renderContext, currentUser) && Validator.isNotEmpty(userName)) {
						user = userManagerService.lookupUser(userName);
						if (user != null) {
							if (StringUtils.equals(targetClass, USER_CLASS_MEMBER)) {
								//remove membrer from PROFESSIONELS
								UserActionUtilities.removeUserFromGroup(renderContext, USER_GROUP_PROFESSIONNEL, user);
								//should not be set already but...
								//...not removed because there are some validations that are looking for 
								//this field (IsProfessional) rather than in isInGroup verification
								user.setProperty(PROPERTIES_USER_IS_MEMBER, Boolean.toString(true));
								user.setProperty(PROPERTIES_USER_IS_PROFESSIONAL, Boolean.toString(false));
								userManagerService.updateCache(user);
								json.put("ok", "ok");
								json.put("redirect", UserHelper.getProfileUrl(renderContext, ((JCRUser) user).getNode(session)));
							} else if (StringUtils.equals(targetClass, USER_CLASS_PROFESSIONAL)) {
								//add to group
								UserActionUtilities.addUserToGroup(renderContext, USER_GROUP_PROFESSIONNEL, user);
								//should not be set already but...
								//...not removed because there are some validations that are looking for 
								//this field (IsProfessional) rather than in isInGroup verification
								user.setProperty(PROPERTIES_USER_IS_PROFESSIONAL, Boolean.toString(true));
								user.setProperty(PROPERTIES_USER_IS_MEMBER, Boolean.toString(false));
								if (StringUtils.isEmpty(user.getProperty("j:function")))
									user.setProperty("j:function", "Professionnel");
								userManagerService.updateCache(user);
								json.put("ok", "ok");
								json.put("redirect", UserHelper.getProfileUrl(renderContext, ((JCRUser) user).getNode(session)));
							} else {
								return new ActionResult(HttpServletResponse.SC_BAD_REQUEST, null, json);
							}
						}
					} else {
						return new ActionResult(HttpServletResponse.SC_FORBIDDEN, null, json);
					}
				} catch (Throwable t) {
					_log.error("Erreur : " + t.getMessage(), t);
				}

				return new ActionResult(HttpServletResponse.SC_OK, null, json);
			}
		});
	}

	/**
	 * JahiaUserManagerService
	 */
	private JahiaUserManagerService userManagerService;

	/**
	 * Bean Property setter
	 * 
	 * @param userManagerService
	 */
	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	private transient static Logger _log = LoggerFactory.getLogger(SwitchUserClassAction.class);

}
