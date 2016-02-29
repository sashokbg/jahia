package org.jahia.modules.ci.helpers;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.query.QueryResultWrapper;

public class RubricsThematicsHelper {

	private static Logger log = Logger.getLogger(RubricsThematicsHelper.class);
	
	/**
	 * Get the preference real ID regardless of its type
	 * Returns empty string if none
	 * 
	 * @param thamticOrRubric
	 * @return
	 */
	public static String getRealId(JCRNodeWrapper thamticOrRubric){
		if(!StringUtils.isEmpty(thamticOrRubric.getPropertyAsString("rubricId"))){
			return thamticOrRubric.getPropertyAsString("rubricId");
		}
		else if(!StringUtils.isEmpty(thamticOrRubric.getPropertyAsString("thematicId"))){
			return thamticOrRubric.getPropertyAsString("thematicId");
		}
		return "";
	}
	
	public static JCRNodeWrapper getThematicById(String thematicId, JCRSessionWrapper session){
		JCRNodeWrapper resultNode = null;
		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			
			StringBuilder query = new StringBuilder("SELECT * FROM [jmix:ciMarkerPageption] where isThematic = true and thematicId = '"+thematicId+"' ");
			
			Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
			QueryResultWrapper queryResult = null;
			
			try {
				queryResult = (QueryResultWrapper) q.execute();
				 queryResult.getNodes().getSize();
			} catch (InvalidQueryException e) {
				log.warn("Invalid query exception : " + query);
			}
			
			NodeIterator iterator;
			iterator = queryResult.getNodes();
			
			//should have one result ONLY
			while(iterator.hasNext()){
				resultNode = (JCRNodeWrapper) iterator.next();
				return resultNode;
			}
		} catch (RepositoryException e) {
			e.printStackTrace();;
		}
		
		return resultNode;
	}
	
	public static JCRNodeWrapper getRubricById(String rubricId, JCRSessionWrapper session){
		JCRNodeWrapper resultNode = null;
		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			
			StringBuilder query = new StringBuilder("SELECT * FROM [jmix:ciMarkerPageption] where isRubric = true and rubricId = '"+rubricId+"' ");
			
			Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
			QueryResultWrapper queryResult = null;
			
			try {
				queryResult = (QueryResultWrapper) q.execute();
				 queryResult.getNodes().getSize();
			} catch (InvalidQueryException e) {
				log.warn("Invalid query exception : " + query);
			}
			
			NodeIterator iterator;
			iterator = queryResult.getNodes();
			
			//should have one result ONLY
			while(iterator.hasNext()){
				resultNode = (JCRNodeWrapper) iterator.next();
				return resultNode;
			}
		} catch (RepositoryException e) {
			e.printStackTrace();;
		}
		
		return resultNode;
	}
	
	/**
	 * Get a thematic or a rubric regardless of it's type
	 * 
	 * Uses real ID and not UUID
	 * 
	 * @param id
	 * @param session
	 * @return
	 */
	public static JCRNodeWrapper getThematicOrRubricById(String id, JCRSessionWrapper session){
		JCRNodeWrapper resultNode = null;
		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			
			StringBuilder query = new StringBuilder("SELECT * FROM [jmix:ciMarkerPageption] where thematicId = '"+id+"' OR rubricId = '"+id+"'");
			
			Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
			QueryResultWrapper queryResult = null;
			
			try {
				queryResult = (QueryResultWrapper) q.execute();
				queryResult.getNodes().getSize();
			} catch (InvalidQueryException e) {
				log.warn("Invalid query exception : " + query);
			}
			
			NodeIterator iterator;
			iterator = queryResult.getNodes();
			
			//should have one result ONLY
			while(iterator.hasNext()){
				resultNode = (JCRNodeWrapper) iterator.next();
				return resultNode;
			}
		} catch (RepositoryException e) {
			e.printStackTrace();;
		}
		
		return resultNode;
	}
}
