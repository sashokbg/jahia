package org.jahia.migrate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MigrateMembersGroovy {

	final static Log log = LogFactory.getLog(MigrateMembersGroovy.class);

	static {
		try {
			org.jahia.services.content.JCRTemplate.getInstance().doExecuteWithSystemSession(new org.jahia.services.content.JCRCallback<Object>() {
				public Object doInJCR(org.jahia.services.content.JCRSessionWrapper session)
						throws javax.jcr.RepositoryException {
					final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("");
					
					log.info("//////\n// START GROOVY SCRIPT - Migrate Membres\n////////");
					log.info("Loading group manager");
					org.jahia.services.usermanager.JahiaGroupManagerService groupManager = org.jahia.registries.ServicesRegistry
							.getInstance().getJahiaGroupManagerService();
					log.info(".. OK");
					log.info("Loading user manager");
					org.jahia.services.usermanager.JahiaUserManagerService userManager = org.jahia.registries.ServicesRegistry
							.getInstance().getJahiaUserManagerService();
					log.info(".. OK");
					log.info("Loading site manager");
					org.jahia.services.sites.JahiaSitesService sitesService = org.jahia.registries.ServicesRegistry.getInstance()
							.getJahiaSitesService();
					log.info(".. OK");
					
					org.jahia.services.sites.JahiaSite site = null;
					try {
						java.util.Iterator<org.jahia.services.sites.JahiaSite> sitesIterator = sitesService.getSites();
						while(sitesIterator.hasNext()){
							org.jahia.services.sites.JahiaSite siteItem = sitesIterator.next();
							if(siteItem.getNode().isNodeType("jmix:ciSiteConfiguration")){
								site = siteItem;
								log.info("site found");
								break;
							}
						}
						
						if(site==null){
							log.error("site 'preparonsmaretraite' not found - ABORT");
							return null;
						}

						log.info("Loading group: membres");
						org.jahia.services.usermanager.JahiaGroup groupMembres = groupManager.lookupGroup(site.getID(), org.jahia.utils.SiteGroups.MEMBERS.getName());
						if (null == groupMembres) {
							log.warn("Group does not exist - creating..");
							groupMembres = groupManager.createGroup(site.getID(),
									org.jahia.utils.SiteGroups.MEMBERS.getName(), null,
									Boolean.FALSE);
							log.info(".. OK");
						} else {
							log.info(".. OK");
						}
						
						log.info("Migrating users to new groups");
						for (String userKey : userManager.getUserList()) {
							org.jahia.services.usermanager.JahiaUser user = userManager
									.lookupUserByKey(userKey);
							if (null != user){
								org.jahia.services.usermanager.UserProperty propMembre = user.getUserProperty(org.commons.util.CiConstants.PROPERTIES_USER_IS_MEMBER);
								if(null != propMembre){
									if(Boolean.TRUE
											.toString().equals(propMembre.getValue())){
										boolean result = groupMembres.addMember(user);
										log.info("adding user member: " + user.getName()+" .."+(result?"OK":"ERR"));
									}
								}
							}
						}
						log.info(".. OK");
					} catch (org.jahia.exceptions.JahiaException e) {
						log.error("Could not create group");
						log.error(" - Group: members");
						e.printStackTrace();
					} catch (javax.jcr.RepositoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					log.info("//////\n// END GROOVY SCRIPT\n////////");

					session.save();
					return null;
				}
			});
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		
	}
}
