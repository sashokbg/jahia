package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.UserData;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.slf4j.Logger;

public class UserProfile extends Action implements CiConstants {

	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(UserProfile.class);

	private JahiaUserManagerService userManagerService;

	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		UserData data = new UserData();
		data.setUser(getCurrentUser());
		setImageMaxSize(data, renderContext);
		UserHelper.populateParams(req, data, parameters);

		updateUser(data);

		if (data.isAvatarRequested()) {
			boolean avatarExists = UserHelper.populateAvatar(data.getAvatarBean(), renderContext, false);
			boolean isAvatarCreated = false;
			if (avatarExists && data.isAvatarRequested())
				isAvatarCreated = UserHelper.manageUserAvatar(session, data);
			if (avatarExists && !isAvatarCreated && data.isAvatarRequested())
				logger.warn("User " + session.getUser().getUsername() + " has requested for avatar loading but this failed.");
		} else if (data.isRemoveAvatar()) {
			UserHelper.deleteCurrentUserAvatar(((JCRUser) getCurrentUser()).getNode(session));
		}

		if (logger.isDebugEnabled())
			logger.debug("User " + session.getUser().getUsername() + " updated his information sucessfully.");
		UserHelper.closeUserRegistration(renderContext, parameters, data, false);
		userManagerService.updateCache(data.getUser());
		session.save();
		req.getSession().setAttribute(PARAM_USER_PROFILE_UPDATED, true);
		return ActionResult.OK;
	}

	private void setImageMaxSize(UserData data, RenderContext renderContext) {
		data.getAvatarBean().setImageMaxSize(UserHelper.getImageUploadMaxSize(renderContext));
	}

	private String[] MODIFIABLE_PROPS = { PROPERTIES_USER_MAIL, PROPERTIES_USER_FIRSTNAME, PROPERTIES_USER_LASTAME,
			PROPERTIES_USER_EMAIL_NOTIF_DIS, PROPERTIES_USER_BIRTH_DATE, PROPERTIES_USER_COUNTRY, PROPERTIES_USER_ZIPCODE,
			PROPERTIES_USER_TYPE, PROPERTIES_USER_RETREAT_YEAR, PROPERTIES_USER_SELECTED_RUBRICS, PROPERTIES_USER_SELECTED_THEMATICS,
			PROPERTIES_USER_ACTIVITY_CHOICE, PROPERTIES_USER_ACCEPT_NOTIF, PROPERTIES_USER_ACCEPT_NEWS, PROPERTIES_USER_ACCEPT_NEWS_GROUP };

	/**
	 * @param parameters
	 * @return
	 * 
	 */
	private void updateUser(UserData data) {
		if (StringUtils.isNotEmpty(data.getPseudoname())
				&& !data.getPseudoname().equals(data.getUser().getProperty(PROPERTIES_USER_PSEUDODENAME))) {
			data.getUser().setProperty(PROPERTIES_USER_PSEUDODENAME, data.getPseudoname());
		}
		for (int i = 0; i < MODIFIABLE_PROPS.length; i++) {
			String key = MODIFIABLE_PROPS[i];
			String newProp = data.getProperties().getProperty(key) != null ? data.getProperties().getProperty(key) : "";
			String oldProp = data.getUser().getProperties().getProperty(key) != null ? data.getUser().getProperties().getProperty(key) : "";
			if (!newProp.equals(oldProp))
				data.getUser().setProperty(key, data.getProperties().getProperty(key));
		}
	}

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

}