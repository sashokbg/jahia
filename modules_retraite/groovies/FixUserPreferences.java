import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FixUserPreferences {

	static {

		
		try {
			org.jahia.services.content.JCRTemplate.getInstance().doExecuteWithSystemSession(new org.jahia.services.content.JCRCallback<Object>() {
				public Object doInJCR(org.jahia.services.content.JCRSessionWrapper session)
						throws javax.jcr.RepositoryException {
			
					final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("");
					
					try{
						int idCount = 0;
						
						log.info("//////\n// GROOVY Fix User Preferences / part 1 - preferences \n////////");
						
						javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
						String query = "SELECT * FROM [jnt:page] WHERE isRubric = 'true'";
	
						javax.jcr.query.Query q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
						javax.jcr.query.QueryResult queryResult = q.execute();
						javax.jcr.NodeIterator iterator = queryResult.getNodes();
						
						log.info("First pass - all rubrics");
						while(iterator.hasNext()) {
							org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
							n.setProperty("rubricId", idCount);
							log.info("UUID ["+n.getIdentifier() + "] TO REAL ID [" + idCount +"]");
							idCount++;
						}
						log.info("..OK");
						
						log.info("First pass - all thematics");
						
						query = "SELECT * FROM [jnt:page] WHERE isThematic = 'true'";
	
						q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
						queryResult = q.execute();
						iterator = queryResult.getNodes();
	
						while (iterator.hasNext()) {
							final org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
							n.setProperty("thematicId", idCount);
							log.info("UUID ["+n.getIdentifier() + "] TO REAL ID [" + idCount +"]");
							idCount++;
						}
						log.info("..OK");
						
						session.save();
					} catch (javax.jcr.query.InvalidQueryException e) {
						e.printStackTrace();
					} catch (javax.jcr.RepositoryException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
		
		
		
	}
}
