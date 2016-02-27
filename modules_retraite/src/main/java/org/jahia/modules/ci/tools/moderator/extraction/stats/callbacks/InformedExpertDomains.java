package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class InformedExpertDomains extends AbstractStatCallback {
	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InformedExpertDomains.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext renderContext) {
		NodeIterator iterator = getMembersNodes(session, renderContext, USER_TYPE_RETRAITE_JUNIOR, MEMBER_UNLOCKED);
		Integer size = 0;
		while (iterator.hasNext()) {
			JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
			try {
				if (node.hasProperty(PROPERTIES_USER_SELECTED_THEMATICS)
						&& StringUtils.isNotEmpty(node.getProperty(PROPERTIES_USER_SELECTED_THEMATICS).getString()))
					size++;
			} catch (RepositoryException e) {
				LOGGER.warn("Cannot get property " + PROPERTIES_USER_SELECTED_THEMATICS + " on node " + node);
			}
		}
		return size;
	}
}
