import javax.jcr.ItemNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FixUserPreferences {

	static {

		//COPY FROM HERE
		try {
			org.jahia.services.content.JCRTemplate.getInstance().doExecuteWithSystemSession(new org.jahia.services.content.JCRCallback<Object>() {
				public Object doInJCR(org.jahia.services.content.JCRSessionWrapper session)
						throws javax.jcr.RepositoryException {
			
					final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("");
					
					try{
						int idCount = 0;
						
						log.info("//////\n// GROOVY Fix User Preferences / part 2 - users \n////////");
						
						log.info("Loading user manager");
						org.jahia.services.usermanager.JahiaUserManagerService userManager = org.jahia.registries.ServicesRegistry
								.getInstance().getJahiaUserManagerService();
						log.info(".. OK");
						
						log.info("Loading users and fixing preferences");
						for (String userKey : userManager.getUserList()) {
							org.jahia.services.usermanager.JahiaUser user = userManager
									.lookupUserByKey(userKey);
							if (null != user){
								String selectedThematics = user.getProperty("selectedThematics");
								String fixedThematics = "";
								
								log.info("Fixing thematics for user: " + user.getName());
								if(null!=selectedThematics){
									String[] oldThematics = selectedThematics.split(",");
									for(int i=0; i< oldThematics.length; i++ ){
										String thematic = oldThematics[i];
										try{
											org.jahia.services.content.JCRNodeWrapper node = session.getNodeByUUID(thematic)
											String realId = node.getPropertyAsString("thematicId");
											fixedThematics+=realId;
											//do not add trailing "," if last element
											if(i<oldThematics.length-1){
												fixedThematics+=",";
											}
											
											log.info("UUID ["+thematic+"] TO Real ID ["+realId+"] ");		 
										}catch(javax.jcr.ItemNotFoundException inf){
											log.error("User thematics not found. Probably already processed, or wrong UUID due to export.");
										}
									}
									user.setProperty("selectedThematics", fixedThematics);
								}
								
								log.info(".. OK");
								log.info("Fixing rubrics for user: " + user.getName());
								
								String selectedRubrics = user.getProperty("selectedRubrics");
								String fixedRubrics = "";
								if(null!=selectedRubrics){
									String[] oldRubrics = selectedRubrics.split(",");
									for(int i=0; i< oldRubrics.length; i++ ){
										String rubric = oldRubrics[i];
										try{
											org.jahia.services.content.JCRNodeWrapper node = session.getNodeByUUID(rubric)
											String realId = node.getPropertyAsString("rubricId");
											fixedRubrics+=realId;
											//do not add trailing "," if last element
											if(i<oldRubrics.length-1){
												fixedRubrics+=",";
											}
											
											log.info("UUID ["+rubric+"] TO Real ID ["+realId+"] ");
										}catch(javax.jcr.ItemNotFoundException inf){
											log.error("User rubrics not found. Probably already processed, or wrong UUID due to export.");
										}
									}
									user.setProperty("selectedRubrics", fixedRubrics);
									
									log.info(".. OK");
								}
							}
						}
						
						
						session.save();
					} catch (javax.jcr.RepositoryException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
		
		
		//END COPY
	}
}
