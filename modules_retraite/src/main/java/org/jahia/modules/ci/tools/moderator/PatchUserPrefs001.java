package org.jahia.modules.ci.tools.moderator;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.log4j.Logger;
import org.jahia.services.content.JCRSessionWrapper;
import org.springframework.stereotype.Component;

@Component
public class PatchUserPrefs001 extends Patch{

	public PatchUserPrefs001() {
		super();
		setName("Fix User Preferences / part 1");
		StringBuilder description = new StringBuilder();
		description
		.append("Ce patch va générer des id rééls au niveau de chaque \n"
				+ "<br/>rubrique et thématique. Ces IDs seront après contribuables \n"
				+ "<br/>et visibles dans la contribution des pages rubriques et thématiques");
		
		setDescription(description.toString());
		setVersion("1.0");
	}
	
	@Override
	public boolean apply(JCRSessionWrapper session) {
		final Logger log = Logger.getLogger(PatchUserPrefs001.class);
		
		try{
			int idCountRubrucs = 0;
			int idCountThematics = 0;
			
			log.info("//////\n// GROOVY Fix User Preferences / part 1 - preferences \n////////");
			
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			String query = "SELECT * FROM [jnt:page] WHERE isRubric = 'true'";

			Query q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
			QueryResult queryResult = q.execute();
			NodeIterator iterator = queryResult.getNodes();
			
			log.info("First pass - all rubrics");
			while(iterator.hasNext()) {
				org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
				n.setProperty("rubricId", idCountRubrucs);
				log.info("UUID ["+n.getIdentifier() + "] TO REAL ID [" + idCountRubrucs +"]");
				idCountRubrucs++;
			}
			log.info("..OK");
			
			log.info("Second pass - all thematics");
			
			query = "SELECT * FROM [jnt:page] WHERE isThematic = 'true'";

			q = queryManager.createQuery(query.toString(), javax.jcr.query.Query.JCR_SQL2);
			queryResult = q.execute();
			iterator = queryResult.getNodes();

			while (iterator.hasNext()) {
				final org.jahia.services.content.JCRNodeWrapper n = (org.jahia.services.content.JCRNodeWrapper) iterator.next();
				n.setProperty("thematicId", idCountThematics);
				log.info("UUID ["+n.getIdentifier() + "] TO REAL ID [" + idCountThematics +"]");
				idCountThematics++;
			}
			log.info("..OK");
			
			session.save();
		} catch (InvalidQueryException e) {
			e.printStackTrace();
			return false;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
