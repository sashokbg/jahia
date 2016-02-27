package org.jahia.modules.ci.user.actions;

import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaUser;

/**
 * 
 * @author balexandrov
 * @description : permet de modifier les propriétés d'un user en tant que moderateur
 */
public class UpdateUserPropertyAction extends UpdateUserPropertyActionAbstract {

	@Override
	protected boolean canModify(RenderContext renderContext, JahiaUser user,
			String userName) {
		return UserHelper.isModerator(renderContext, user);
	}
}
