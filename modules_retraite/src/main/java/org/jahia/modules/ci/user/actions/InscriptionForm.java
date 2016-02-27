package org.jahia.modules.ci.user.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.CiConstants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.UserData;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.params.ProcessingContext;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerRoutingService;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;

public class InscriptionForm extends Action implements CiConstants {

	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(InscriptionForm.class);
	private JahiaUserManagerService userManagerService;

	public JahiaUserManagerService getUserManagerService() {
		return userManagerService;
	}

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		final String siteKey = renderContext.getSite().getSiteKey();
		UserData data = new UserData();
		setImageMaxSize(data, renderContext);
		parameters.put(siteKey, Arrays.asList(new String[] { siteKey }));
		UserHelper.populateParams(req, data, parameters);

		if (logger.isInfoEnabled())
			logger.info("ciInscription - Initiating inscription for user with session ID : " + req.getSession().getId());


		boolean isUserCreated = manageUserCreation(data, siteKey);
		if (!isUserCreated) {
			String jcrErrorRedirectTo = getParameter(parameters, "jcrErrorRedirectTo");
			parameters.remove("jcrRedirectTo");
			req.getSession().setAttribute("error", "error");
			return new ActionResult(HttpServletResponse.SC_OK, jcrErrorRedirectTo);
		}

		JCRSessionFactory.getInstance().setCurrentUser(data.getUser());
		req.getSession().setAttribute(ProcessingContext.SESSION_USER, data.getUser());
		session = JCRSessionFactory.getInstance().getCurrentUserSession();

		if (data.isAvatarRequested()) {
			boolean avatarExists = UserHelper.populateAvatar(data.getAvatarBean(), renderContext, false);
			boolean isAvatarCreated = false;
			if (avatarExists && data.isAvatarRequested())
				isAvatarCreated = UserHelper.manageUserAvatar(session, data);
			if (avatarExists && !isAvatarCreated && data.isAvatarRequested())
				logger.warn("User " + session.getUser().getUsername() + " has requested for avatar loading but this failed.");
		}

		if (isUserCreated) {
			if (logger.isInfoEnabled())
				logger.info("User " + session.getUser().getUsername() + " successfully registered.");
			data.getUser().setProperty(siteKey, Boolean.TRUE.toString());
			JahiaGroupManagerRoutingService groupService = JahiaGroupManagerRoutingService.getInstance();
			if (groupService.groupExists(renderContext.getSite().getID(), siteKey) && data.getUser() != null) {
				JahiaGroup group = groupService.lookupGroup(renderContext.getSite().getID(), siteKey);
				if (group != null) {
					group.addMember(data.getUser());
				}
			}

			UserHelper.closeUserRegistration(renderContext, parameters, data, true);
			// flag for the tracker web performance
			req.getSession().setAttribute("JUST_CREATED", true);
			return ActionResult.OK;
		}
		logger.error("Seems that user inscription did not finished normally : " + data);
		return ActionResult.INTERNAL_ERROR;

	}

	private void setImageMaxSize(UserData data, RenderContext renderContext) {
		data.getAvatarBean().setImageMaxSize(UserHelper.getImageUploadMaxSize(renderContext));
	}

	/**
	 * @param parameters
	 * @return
	 * 
	 */
	private boolean manageUserCreation(UserData data, String siteKey) {
		String nodename = siteKey + "_" + data.getPseudoname();
		Properties userSearchCriterias = new Properties();
		userSearchCriterias.put("j:email", data.getEmail());
		if (userManagerService.lookupUser(nodename) != null || !userManagerService.searchUsers(userSearchCriterias).isEmpty()) {
			return false;
		}
		JahiaUser user = userManagerService.createUser(nodename, data.getPassword(), data.getProperties());
		logger.debug("user created: " + user);
		data.setUser(user);

		return user != null;
	}

}