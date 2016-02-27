package org.jahia.modules.ci.tools.moderator;

import java.security.Principal;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.log4j.Logger;
import org.commons.util.CiConstants;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.UserProperty;
import org.jahia.utils.SiteGroups;
import org.jahia.utils.SiteRoles;
import org.springframework.stereotype.Component;

@Component
public class PatchUserGroups001 extends Patch {
	private static final int USERS_PER_GROUP = 300;
	final Logger log = Logger.getLogger(PatchUserGroups001.class);

	public PatchUserGroups001() {
		super();
		setName("Patch User Groups");
		setDescription("Ce patch va diviser les utilisateurs en sous groups de 300 utilisateurs et les mettres dans les bons groups");
		setVersion("1.0");
	}

	@Override
	public boolean apply(JCRSessionWrapper session) {
		try {
//			JCRTemplate.getInstance().doExecuteWithSystemSession(new org.jahia.services.content.JCRCallback<Object>() {
//				public Object doInJCR(org.jahia.services.content.JCRSessionWrapper session)
//						throws RepositoryException {
					
					log.info("//////\n// START GROOVY SCRIPT - Divide into groups\n////////");
					log.info("Loading group manager");
					JahiaGroupManagerService groupManager = ServicesRegistry
							.getInstance().getJahiaGroupManagerService();
					log.info(".. OK");
					log.info("Loading user manager");
					JahiaUserManagerService userManager = ServicesRegistry
							.getInstance().getJahiaUserManagerService();
					log.info(".. OK");
					log.info("Loading site manager");
					JahiaSitesService sitesService = ServicesRegistry.getInstance()
							.getJahiaSitesService();
					log.info(".. OK");
					
					JahiaSite site = null;
					try {
						java.util.Iterator<JahiaSite> sitesIterator = sitesService.getSites();
						while(sitesIterator.hasNext()){
							JahiaSite siteItem = sitesIterator.next();
							if(siteItem.getNode().isNodeType("jmix:ciSiteConfiguration")){
								site = siteItem;
								log.info("site found");
								break;
							}
						}
						
						if(site==null){
							log.error("site 'preparonsmaretraite' not found - ABORT");
							return false;
						}
						
						log.info("Site loaded - id: " + site.getID() + " key: "
								+ site.getSiteKey());
						
						log.info("Casting to Jcr Site Node");
						JCRSiteNode jcrSite = (JCRSiteNode) site.getNode();
						log.info(".. OK");
						
						log.info("-- ------ --");
						log.info("-- GROUPS --");
						log.info("-- ------ --");
						
						log.info("Loading group: preparonsmaretraite");
						JahiaGroup groupMain = groupManager.lookupGroup(site.getID(), "preparonsmaretraite");
						if (null == groupMain) {
							log.warn("Group does not exist - END..");
							return false;
						} else {
							log.info(".. OK");
						}
						
						log.info("Loading group: membres");
						JahiaGroup groupMembres = groupManager.lookupGroup(site.getID(), SiteGroups.MEMBERS.getName());
						if (null == groupMembres) {
							log.warn("Group does not exist - creating..");
							groupMembres = groupManager.createGroup(site.getID(),
									SiteGroups.MEMBERS.getName(), null, Boolean.FALSE);
							log.info(".. OK");
						} else {
							log.info(".. OK");
						}
						
						log.info("Loading group: professionnels");
						JahiaGroup groupProfs = groupManager.lookupGroup(site.getID(), SiteGroups.PROFESSIONALS.getName());
						if (null == groupProfs) {
							log.warn("Group does not exist - creating..");
							groupProfs = groupManager.createGroup(site.getID(),SiteGroups.PROFESSIONALS.getName(), null, Boolean.FALSE);
							log.info(".. OK");
						} else {
							log.info(".. OK");
						}
						
						log.info("-- ---------- --");
						log.info("-- END GROUPS --");
						log.info("-- ---------- --");
						
						log.info("Migrating users to new groups - separating into subgroups of 300");
						
						int counter = 0;
						int subGroupCounter = 0;
						JahiaGroup subGroup = null;
						
						for (Principal userPrincipal : groupMain.getRecursiveUserMembers()) {
							JahiaUser user = userManager.lookupUser(userPrincipal.getName());
							if (null != user){
								UserProperty propMembre = user.getUserProperty(CiConstants.PROPERTIES_USER_IS_MEMBER);
								UserProperty propProf = user.getUserProperty(CiConstants.PROPERTIES_USER_IS_PROFESSIONAL);
								
								//MEMBERS
								if(null != propMembre){
									if(Boolean.TRUE.toString().equals(propMembre.getValue())){
										if(counter%USERS_PER_GROUP==0){
											log.info("Creating sub group number: "+subGroupCounter);
											Properties subGroupProps = new Properties();
											subGroupProps.setProperty("groupType", "subgroup");
											subGroup = groupManager.createGroup(site.getID(), "preparonsmaretraite-"+subGroupCounter, subGroupProps, Boolean.FALSE);
											groupMembres.addMember(subGroup);
											log.info(".. OK");
											subGroupCounter++;
										}
										counter++;
										if (null != user && null != subGroup){
											log.info("Moving user ["+user.getName()+"] to subgroup: ["+subGroup.getGroupname()+"]");
											boolean result = subGroup.addMember(user);
											log.info("adding user member: " + user.getName()+" .."+(result?"OK":"ERR"));
										}
									}
								}// PROFESSIONALS (no subgrouping here for now)
								else if(null != propProf){
									if(Boolean.TRUE.toString().equals(propProf.getValue())){
										if (null != user){
											boolean result = groupProfs.addMember(user);
											log.info("adding user prof: " + user.getName()+" .."+(result?"OK":"ERR"));
										}
									}
								}
								
								if(groupMain.removeMember(user))
									log.info(".. OK");
								else
									log.error("KO - Problem adding member to group");
							}
						}
						log.info(".. OK");
						
						log.info("Grant roles to group in site context");
						
						log.info(" - Role: "+SiteRoles.PROFESSIONAL);
						log.info(" - Group: " + groupProfs.getGroupname());
						Set<String> roles = new HashSet<String>();
						roles.add(SiteRoles.PROFESSIONAL.getName());
						jcrSite.grantRoles("g:" + groupProfs.getGroupname(), roles);
						
						groupMain.addMember(groupProfs);
						groupMain.addMember(groupMembres);
						
						jcrSite.save();
						log.info(".. OK");

					} catch (RepositoryException e) {
						e.printStackTrace();
						return false;
					} catch (JahiaException e) {
						e.printStackTrace();
					}

					session.save();
					log.info("//////\n// END GROOVY SCRIPT\n////////");
		}catch (AccessDeniedException e) {
			e.printStackTrace();
			return false;
		} catch (ItemExistsException e) {
			e.printStackTrace();
			return false;
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidItemStateException e) {
			e.printStackTrace();
			return false;
		} catch (VersionException e) {
			e.printStackTrace();
			return false;
		} catch (LockException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchNodeTypeException e) {
			e.printStackTrace();
			return false;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
