import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateUserGroovy {

	final static Log log = LogFactory.getLog(MapGroovy.class);

	static {
		log.info("//////\n// GROOVY RUBRIC \n////////");

		try {
			org.jahia.services.content.JCRSessionWrapper session = org.jahia.services.content.JCRSessionFactory.getInstance()
					.getCurrentUserSession("default");
			javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
			String query = "SELECT * FROM [jnt:page] WHERE isRubric = 'true'";

			javax.jcr.query.Query q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult queryResult = q.execute();
			javax.jcr.NodeIterator iterator = queryResult.getNodes();

			while (iterator.hasNext()) {

				final org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
				log.info(n.getIdentifier() + " : " + n.getPath());

			}
		} catch (javax.jcr.query.InvalidQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("//////\n// GROOVY THEMATIC \n////////");
		try {
			org.jahia.services.content.JCRSessionWrapper session = org.jahia.services.content.JCRSessionFactory.getInstance()
					.getCurrentUserSession("default");
			javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
			String query = "SELECT * FROM [jnt:page] WHERE isThematic = 'true'";

			javax.jcr.query.Query q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
			javax.jcr.query.QueryResult queryResult = q.execute();
			javax.jcr.NodeIterator iterator = queryResult.getNodes();

			while (iterator.hasNext()) {

				final org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
				log.info(n.getIdentifier() + " : " + n.getPath());

			}
		} catch (javax.jcr.query.InvalidQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
