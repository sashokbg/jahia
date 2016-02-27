package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class KFPostRated extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KFPostRated.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		try {
			QueryResult queryResult = executeSQLQuery(session, "select * from [jmix:ciRated] as reply", 0, 0);
			NodeIterator iterator = queryResult.getNodes();
			long scores = 0L;
			while (iterator.hasNext()) {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				if (node.hasProperty("score")) {
					JCRPropertyWrapper property = node.getProperty("score");
					if (property != null) {
						long score = property.getLong();
						scores += score;
					}
				}
			}
			return scores;
		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return 0;
	}

}
