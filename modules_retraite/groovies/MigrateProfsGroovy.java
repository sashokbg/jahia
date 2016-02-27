package org.jahia.migrate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MigrateProfsGroovy {

	final static Log log = LogFactory.getLog(MigrateProfsGroovy.class);

	static {
		try {
			org.jahia.services.content.JCRTemplate.getInstance().doExecuteWithSystemSession(new org.jahia.services.content.JCRCallback<Object>() {
				public Object doInJCR(org.jahia.services.content.JCRSessionWrapper session)
						throws javax.jcr.RepositoryException {
					final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("");
					
					log.info("//////\n// START GROOVY SCRIPT - Migrate Professionals\n////////");
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
						
						log.info("Site loaded - id: " + site.getID() + " key: "
								+ site.getSiteKey());
						site.getNode();
						log.info("Casting to Jcr Site Node");
						org.jahia.services.content.decorator.JCRSiteNode jcrSite = (org.jahia.services.content.decorator.JCRSiteNode) site.getNode();
						log.info(".. OK");

						log.info("Loading group: professionnels");
						org.jahia.services.usermanager.JahiaGroup group = groupManager.lookupGroup(site.getID(), org.jahia.utils.SiteGroups.PROFESSIONALS.getName());
						if (null == group) {
							log.warn("Group does not exist - creating..");
							group = groupManager.createGroup(site.getID(),
									org.jahia.utils.SiteGroups.PROFESSIONALS.getName(), null,
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
								org.jahia.services.usermanager.UserProperty prop = user.getUserProperty(org.commons.util.CiConstants.PROPERTIES_USER_IS_PROFESSIONAL);
								if(null != prop){
									if(Boolean.TRUE
											.toString().equals(prop.getValue())){
										boolean result = group.addMember(user);
										log.info("adding user prof: " + user.getName()+" .."+(result?"OK":"ERR"));
									}
								}
							}
						}
						log.info(".. OK");
						
						log.info("Grant roles to group in site context");
						log.info(" - Role: "+org.jahia.utils.SiteRoles.PROFESSIONAL);
						log.info(" - Group: " + group.getGroupname());
						java.util.Set<String> roles = new java.util.HashSet<String>();
						roles.add(org.jahia.utils.SiteRoles.PROFESSIONAL.getName());
						jcrSite.grantRoles("g:" + group.getGroupname(), roles);
						jcrSite.save();
						log.info(".. OK");

					} catch (org.jahia.exceptions.JahiaException e) {
						log.error("Could not create group");
						log.error(" - Group: professionnels");
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
