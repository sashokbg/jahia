package org.jahia.modules.ci.tools.moderator;

import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

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
import org.jahia.utils.SiteRoles;
import org.springframework.stereotype.Component;

@Component
public class PatchMigrateProfessionals extends Patch {

	private static final String PROFESSIONAL_ROLE = SiteRoles.PROFESSIONAL;

	public PatchMigrateProfessionals() {
		super();
		setName("Migrate Professionals");
		StringBuilder description = new StringBuilder();
		description
				.append("Ce patch va créer une nouvelle groupe des membres (Professionals) et "
						+ "garantir ses roles.");
		setDescription(description.toString());
		setVersion("1.5");
	}

	@Override
	public boolean apply(JCRSessionWrapper session) {
		final Logger log = Logger.getLogger(PatchMigrateProfessionals.class);

		try {

			log.info("//////\n// START GROOVY SCRIPT - Migrate Professionals\n////////");

			JahiaSite site = PatchUtilities.findPreparonsMaRetraite();

			log.info("Loading user manager");
			JahiaUserManagerService userManager = ServicesRegistry
					.getInstance().getJahiaUserManagerService();

			if (site == null) {
				log.error("site 'preparonsmaretraite' not found - ABORT");
				return Boolean.FALSE;
			}

			log.info("Loading group manager");
			JahiaGroupManagerService groupManager = ServicesRegistry
					.getInstance().getJahiaGroupManagerService();
			log.info(".. OK");
			log.info("Site loaded - id: " + site.getID() + " key: "
					+ site.getSiteKey());
			site.getNode();
			log.info("Casting to Jcr Site Node");
			JCRSiteNode jcrSite = (JCRSiteNode) site.getNode();
			log.info(".. OK");

			log.info("Loading group: professionnels");

			JahiaGroup group = groupManager.lookupGroup(site.getID(),
					CiConstants.USER_GROUP_PROFESSIONNEL);
			if (null == group) {
				log.warn("Group does not exist - creating..");
				group = groupManager.createGroup(site.getID(),
						"professionnels", null, Boolean.FALSE);
				log.info(".. OK");
			} else {
				log.info(".. OK");
			}

			log.info("Migrating users to new groups");
			for (String userKey : userManager.getUserList()) {
				JahiaUser user = userManager.lookupUserByKey(userKey);
				if (null != user) {
					UserProperty prop = user
							.getUserProperty(CiConstants.PROPERTIES_USER_IS_PROFESSIONAL);
					if (null != prop) {
						if (Boolean.TRUE.toString().equals(prop.getValue())) {
							boolean result = group.addMember(user);
							log.info("adding user prof: " + user.getName()
									+ " .." + (result ? "OK" : "ERR"));
						}
					}
				}
			}
			log.info(".. OK");

			log.info("Grant roles to group in site context");
			log.info(" - Role: " + PROFESSIONAL_ROLE);
			log.info(" - Group: " + group.getGroupname());
			java.util.Set<String> roles = new java.util.HashSet<String>();
			roles.add(PROFESSIONAL_ROLE);
			jcrSite.grantRoles("g:" + group.getGroupname(), roles);
			jcrSite.save();
			log.info(".. OK");

			session.save();
		} catch (JahiaException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (InvalidQueryException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
