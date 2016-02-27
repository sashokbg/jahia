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
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.slf4j.Logger;

public class MostViewedThematics extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MostViewedThematics.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		ValueComparator bvc = new ValueComparator();
		Map<String, Integer> mostActiveThematics = new HashMap<String, Integer>();
		Map<String, Integer> mostActiveThematicsOrdered = new TreeMap<String, Integer>(bvc);
		bvc.setBase(mostActiveThematics);
		try {
			QueryResult thematicResult = executeSQLQuery(session,
					"select * from [jnt:page] as thematics where thematics.isThematic='true'", 0, 0);

			NodeIterator thematicNodes = thematicResult.getNodes();

			while (thematicNodes.hasNext()) {
				JCRNodeWrapper thematicNode = (JCRNodeWrapper) thematicNodes.next();

				Long viewed = 0L;
				if (thematicNode.hasProperty(PROPERTIES_SITE_NB_OF_VIEWS))
					viewed = thematicNode.getProperty(PROPERTIES_SITE_NB_OF_VIEWS).getLong();
				
				JCRNodeWrapper rubricPage = JCRTagUtils.getParentOfType(thematicNode, "jnt:page");

				if (thematicNode != null && rubricPage != null) {
					mostActiveThematics.put(
							getThematicFullName(thematicNode, rubricPage),
							viewed.intValue());
				}
			}
			mostActiveThematicsOrdered.putAll(mostActiveThematics);

		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return new TreeMap<String, Integer>(mostActiveThematicsOrdered);
	}

}
