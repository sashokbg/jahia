package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf;

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class KFMarkettingOffers extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KFMarkettingOffers.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		try {
			QueryResult queryResult = executeSQLQuery(session, "select * from [jnt:user] as user where user.isMember='true' and user."
					+ context.getSite().getSiteKey() + "='true' and " + extractInUuidClause(getModeratorsUuids(context), "user", "<>")
					+ " and user." + PROPERTIES_USER_ACCEPT_NEWS_GROUP + "='true'", 0, 0);
			long size = queryResult.getNodes().getSize();
			return size;
		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return 0;
	}

}
