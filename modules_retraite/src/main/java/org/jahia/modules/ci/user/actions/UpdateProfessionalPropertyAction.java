package org.jahia.modules.ci.user.actions;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaUser;

public class UpdateProfessionalPropertyAction extends
UpdateUserPropertyActionAbstract {

	@Override
	protected boolean canModify(RenderContext renderContext, JahiaUser pro,
			String userName) {
		if (pro == null) {
			return Boolean.FALSE;
		}
		if (StringUtils.isEmpty(userName)) {
			return Boolean.FALSE;
		}
		if (UserHelper.isProfessional(renderContext, pro)) {
			return isSameUser(pro, userName);
		}
		return Boolean.FALSE;
	}

	/**
	 * Returns true if the userName is the same name as the JahiaUser'name
	 * 
	 * @return boolean
	 */
	private boolean isSameUser(JahiaUser user1, String userName) {
		return user1.getUserKey().equals(userManagerService.lookupUser(userName).getUserKey());
	}

}
