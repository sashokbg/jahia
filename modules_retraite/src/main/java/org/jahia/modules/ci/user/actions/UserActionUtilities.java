package org.jahia.modules.ci.user.actions;

import org.jahia.registries.ServicesRegistry;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.services.usermanager.JahiaUser;

public class UserActionUtilities {

	private static JahiaGroup initGroup(RenderContext renderContext,
			String groupName) {
		JahiaGroupManagerService groupManager = ServicesRegistry.getInstance()
				.getJahiaGroupManagerService();
		return groupManager.lookupGroup(renderContext.getSite().getID(),
				groupName);
	}

	public static boolean addUserToGroup(RenderContext renderContext,
			String groupName, JahiaUser user) {
		return initGroup(renderContext, groupName).addMember(user);
	}
	
	public static boolean removeUserFromGroup(RenderContext renderContext,
			String groupName, JahiaUser user) {
		return initGroup(renderContext, groupName).removeMember(user);
	}
}
