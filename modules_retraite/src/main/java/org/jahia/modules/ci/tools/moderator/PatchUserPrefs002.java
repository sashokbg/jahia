package org.jahia.modules.ci.tools.moderator;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.log4j.Logger;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.springframework.stereotype.Component;

@Component
public class PatchUserPrefs002 extends Patch {
	final Logger log = Logger.getLogger(PatchUserPrefs002.class);

	public PatchUserPrefs002() {
		super();
		setName("Fix User Preferences / part 2");
		setDescription("Ce patch doit être executé après le patch \"Fix User Preferences / part 2\""
				+ "<br/>Il va lié tous les utilisateurs avec lers thématiques et rubriques préférés en"
				+ "<br/>utilisant des real IDs");
		setVersion("1.0");
	}

	@Override
	public boolean apply(JCRSessionWrapper session) {
		try {
			log.info("//////\n// GROOVY Fix User Preferences / part 2 - users \n////////");

			log.info("Loading user manager");
			JahiaUserManagerService userManager = ServicesRegistry
					.getInstance().getJahiaUserManagerService();
			log.info(".. OK");

			log.info("Loading users and fixing preferences");
			for (String userKey : userManager.getUserList()) {
				JahiaUser user = userManager.lookupUserByKey(userKey);
				if (null != user) {
					String selectedThematics = user.getProperty("selectedThematics");
					String fixedThematics = "";

					log.info("Fixing thematics for user: " + user.getName());
					if (null != selectedThematics) {
						String[] oldThematics = selectedThematics.split(",");
						for (int i = 0; i < oldThematics.length; i++) {
							String thematic = oldThematics[i];
							try {
								JCRNodeWrapper node = session.getNodeByUUID(thematic);
								String realId = node.getPropertyAsString("thematicId");
								fixedThematics += realId;
								// do not add trailing "," if last element
								if (i < oldThematics.length - 1) {
									fixedThematics += ",";
								}

								log.info("UUID [" + thematic + "] TO Real ID [" + realId + "] ");
							} catch (ItemNotFoundException inf) {
								log.error("User thematics not found. Probably already processed, or wrong UUID due to export.");
							}
						}
						user.setProperty("selectedThematics", fixedThematics);
					}

					log.info(".. OK");
					log.info("Fixing rubrics for user: " + user.getName());

					String selectedRubrics = user.getProperty("selectedRubrics");
					String fixedRubrics = "";
					if (null != selectedRubrics) {
						String[] oldRubrics = selectedRubrics.split(",");
						for (int i = 0; i < oldRubrics.length; i++) {
							String rubric = oldRubrics[i];
							try {
								JCRNodeWrapper node = session.getNodeByUUID(rubric);
								String realId = node.getPropertyAsString("rubricId");
								fixedRubrics += realId;
								// do not add trailing "," if last element
								if (i < oldRubrics.length - 1) {
									fixedRubrics += ",";
								}

								log.info("UUID [" + rubric + "] TO Real ID [" + realId + "] ");
							} catch (ItemNotFoundException inf) {
								log.error("User rubrics not found. Probably already processed, or wrong UUID due to export.");
							}
						}
						user.setProperty("selectedRubrics", fixedRubrics);

						log.info(".. OK");
					}
				}
			}

			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
