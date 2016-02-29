package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class MostViewedRubrics extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MostViewedRubrics.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		ValueComparator bvc = new ValueComparator();
		Map<String, Integer> mostActiveRubrics = new HashMap<String, Integer>();
		Map<String, Integer> mostActiveRubricsOrdered = new TreeMap<String, Integer>(bvc);
		bvc.setBase(mostActiveRubrics);
		try {
			QueryResult rubricResult = executeSQLQuery(session, "select * from [jnt:page] as thematics where thematics."
					+ PROPERTIES_PAGE_ISRUBRIC + "='true'", 0, 0);
			NodeIterator rubricNodes = rubricResult.getNodes();
			while (rubricNodes.hasNext()) {
				JCRNodeWrapper rubricNode = (JCRNodeWrapper) rubricNodes.next();
				Long viewed = 0L;

				QueryResult thematicsResult = executeSQLQuery(session, "select * from [jnt:page] as thematics where thematics."
						+ PROPERTIES_PAGE_ISTHEMATIC + "='true' and  isdescendantnode(thematics, [" + rubricNode.getPath() + "])", 0, 0);
				NodeIterator thematicsNodes = thematicsResult.getNodes();
				while (thematicsNodes.hasNext()) {
					JCRNodeWrapper thematicNode = (JCRNodeWrapper) thematicsNodes.next();
					if (thematicNode.hasProperty(PROPERTIES_SITE_NB_OF_VIEWS))
						viewed += thematicNode.getProperty(PROPERTIES_SITE_NB_OF_VIEWS).getLong();
				}

				mostActiveRubrics.put(getPageTitle(rubricNode), viewed.intValue());
			}
			mostActiveRubricsOrdered.putAll(mostActiveRubrics);

		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return new TreeMap<String, Integer>(mostActiveRubricsOrdered);
	}
}
