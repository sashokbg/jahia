package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class EvolutionAnswersCountFR extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EvolutionAnswersCountFR.class);

	// date du jout
	private Date maxDate;

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		Map<Date, Integer> questionDateMap = new HashMap<Date, Integer>();

		QueryResult queryResult = null;
		NodeIterator nodes = null;

		String query = "select * from [jnt:ciReply]";

		try {
			queryResult = executeSQLQuery(session, query, 0, 0);
			if (queryResult != null)
				nodes = queryResult.getNodes();
		} catch (RepositoryException e1) {
			LOGGER.error("Error on exectuting query : " + query);
		}

		if (nodes != null) {
			List<String> userUuids = getMembersUuids(session, context, USER_TYPE_FUTUR_RETRAITE, MEMBER_WHATEVER_THE_LOCK);
			while (nodes.hasNext()) {
				JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
				try {
					JCRNodeWrapper user = (JCRNodeWrapper) node.getProperty("user").getNode();
					if (userUuids.contains(user.getIdentifier())) {

						Date date = node.getCreationDateAsDate();
						Date simplifiedDate = simplifiedDayDate(date);

						maxDate = new Date();
						if ((AbstractExtractor.minDate.before(simplifiedDate) || AbstractExtractor.minDate.equals(simplifiedDate))
								&& (maxDate.after(simplifiedDate) || maxDate.equals(simplifiedDate))) {
							if (!questionDateMap.containsKey(simplifiedDate)) {
								questionDateMap.put(simplifiedDate, 0);
							}
							questionDateMap.put(simplifiedDate, questionDateMap.get(simplifiedDate) + 1);
							if (LOGGER.isDebugEnabled())
								LOGGER.debug(simplifiedDate + " : " + node.getName() + "[" + questionDateMap.get(simplifiedDate) + "]");
						}
					}
				} catch (RepositoryException e) {
					LOGGER.error("Error on getting creation date for member : " + node);
				}
			}

		}
		return questionDateMap;
	}

}
