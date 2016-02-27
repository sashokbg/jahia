package org.jahia.modules.ci.user.actions;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.params.ProcessingContext;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAction extends Action implements CiConstants {

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		ActionResult resultKo = new ActionResult(HttpServletResponse.SC_ACCEPTED, null, new JSONObject("{KO:KO}"));
		ActionResult resultOk = new ActionResult(HttpServletResponse.SC_ACCEPTED, null, new JSONObject("{OK:OK}"));

		String username = getParameter(parameters, PARAM_USER_USERNAME, "");
		String password = getParameter(parameters, PARAM_USER_PASSWORD);
		String siteKey = renderContext.getSite().getSiteKey();

		JahiaUser user = null;

		if (Validator.isEmailAddress(username)) {

			Properties prop = new Properties();
			prop.setProperty(PROPERTIES_USER_MAIL, username);

			Set<Principal> set = userManagerService.searchUsers(prop);

			if (!set.isEmpty()) {
				Iterator<Principal> it = set.iterator();
				while (it.hasNext()) {
					user = userManagerService.lookupUser(it.next().getName());
					if (Boolean.TRUE.toString().equals(user.getProperty(renderContext.getSite().getSiteKey()))) {
						break;
					}
				}
			}
			
			if (user != null && UserHelper.isAccountLocked(user)) {
				if (LOGGER.isWarnEnabled() && user.verifyPassword(password))
					LOGGER.warn(siteKey + " - User " + username + " trying to log with a locked account.");
				return resultKo;
			}

			if (user != null && user.verifyPassword(password) 
					&& StringUtils.equalsIgnoreCase(user.getProperty(PROPERTIES_USER_MAIL), username)
					&& Boolean.TRUE.toString().equals(user.getProperty(renderContext.getSite().getSiteKey()))) {
				JCRSessionFactory.getInstance().setCurrentUser(user);
				req.getSession().setAttribute(ProcessingContext.SESSION_USER, user);
				if (LOGGER.isInfoEnabled())
					LOGGER.info(siteKey + " - User " + user.getUsername() + " successfully authentified on "
							+ renderContext.getSite().getName() + " site.");
				return resultOk;
			} else if (user != null && user.verifyPassword(password) 
					&& StringUtils.equalsIgnoreCase(user.getProperty(PROPERTIES_USER_MAIL), username)
					&& UserHelper.isModerator(renderContext, user)) {
				JCRSessionFactory.getInstance().setCurrentUser(user);
				req.getSession().setAttribute(ProcessingContext.SESSION_USER, user);
				if (LOGGER.isInfoEnabled())
					LOGGER.info(siteKey + " - Moderator " + user.getUsername() + " successfully authentified on "
							+ renderContext.getSite().getName() + " site.");
				return resultOk;
			}
		}

		if (LOGGER.isWarnEnabled() && user == null)
			LOGGER.warn(siteKey + " - User " + username + " does not exist.");
		if (LOGGER.isWarnEnabled() && user != null) {
			String encryptPassword = JahiaUserManagerService.encryptPassword(password);
			LOGGER.warn(siteKey + " - User " + username + " failed to log with password [" + encryptPassword + "].");
		}

		return resultKo;
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

	private transient static Logger LOGGER = LoggerFactory.getLogger(LoginAction.class);

}
