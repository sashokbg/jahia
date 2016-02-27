package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf;

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class KFAskedQuestions extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KFAskedQuestions.class);

	public Object getData(JCRSessionWrapper session, RenderContext renderContext) {
		try {
			QueryResult queryResult = executeSQLQuery(session, "select * from [jnt:ciQuestion]", 0, 0);
			Long size = queryResult.getNodes().getSize();

			return size;
		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return 0;
	}


}
