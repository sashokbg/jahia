package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.util.Map;
import java.util.TreeMap;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class PreferedRubricsFR extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PreferedRubricsFR.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext renderContext) {
		Map<String, Integer> thematicMap = new TreeMap<String, Integer>();

		QueryResult thematicResult;
		try {
			thematicResult = executeSQLQuery(session, "select * from [jnt:user] as user where user.isMember='true' and user.userType ='"
					+ USER_TYPE_FUTUR_RETRAITE + "'", 0, 0);

			NodeIterator iterator = thematicResult.getNodes();
			while (iterator.hasNext()) {
				JCRNodeWrapper user = (JCRNodeWrapper) iterator.next();

				// recuperation des thematiques
				String userThematic = null;
				if (user.hasProperty(PROPERTIES_USER_SELECTED_RUBRICS))
					userThematic = user.getProperty(PROPERTIES_USER_SELECTED_RUBRICS).getString();

				// recherche des thematiques
				if (StringUtils.isNotEmpty(userThematic)) {
					String delem = ",";
					for (String part : StringUtils.split(userThematic, delem)) {
						try {
							JCRNodeWrapper node = session.getNodeByUUID(part);
							String thematic = getPageTitle(node);
							if (!thematicMap.containsKey(thematic)) {
								thematicMap.put(thematic, 0);
							}
							if (part != null && !part.equals("")) {
								thematicMap.put(thematic, thematicMap.get(thematic) + 1);
							}
						} catch (RepositoryException e) {
							LOGGER.debug("item not found :" + part);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return thematicMap;
	}

}
