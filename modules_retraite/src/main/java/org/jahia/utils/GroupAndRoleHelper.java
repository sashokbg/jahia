package org.jahia.utils;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.services.usermanager.JahiaUser;

public class GroupAndRoleHelper {
	
	private static JahiaGroupManagerService jahiaGroupManagerService = ServicesRegistry.getInstance().getJahiaGroupManagerService();

	public static JahiaGroup addUserToGroup(JahiaUser user, JCRSiteNode site, SiteGroups group) throws RepositoryException{
		//Create group if it does not exist
		if(!jahiaGroupManagerService.groupExists(site.getID(), group.getName())){
			JahiaGroup gr = jahiaGroupManagerService.createGroup(site.getID(), group.getName(),null,Boolean.FALSE);
			if(group == SiteGroups.PROFESSIONALS){
				Set<String> roles = new HashSet<String>();
				roles.add(SiteRoles.PROFESSIONAL.getName());
				site.grantRoles("g:"+gr.getGroupname(), roles);
				site.save();
			}
		}
		
		//add user to group
		JahiaGroup jahiaGroup = jahiaGroupManagerService.lookupGroup(site.getID(), group.getName());
		jahiaGroup.addMember(user);
		
		return jahiaGroup;
	}
	
	public static JahiaGroup removeUserFromGroup(JahiaUser user, JCRSiteNode site, SiteGroups group){
		//Create group if it does not exist
		if(!jahiaGroupManagerService.groupExists(site.getID(), group.getName())){
			 jahiaGroupManagerService.createGroup(site.getID(), group.getName(),null,Boolean.FALSE);
		}
		
		//add user to group
		JahiaGroup jahiaGroup = jahiaGroupManagerService.lookupGroup(site.getID(), group.getName());
		jahiaGroup.removeMember(user);
		
		return jahiaGroup;
	}
}
