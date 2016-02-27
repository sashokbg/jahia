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

public class KFPostedRepliesProfessional extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KFPostedRepliesProfessional.class);

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		try {
			QueryResult queryResult = executeSQLQuery(session, "select * from [jnt:ciReply]", 0, 0);
			long size = 0L;
			if (queryResult.getNodes().getSize() != 0) {
				// distinction des réponses
				NodeIterator iterator = queryResult.getNodes();
				while (iterator.hasNext()) {
					JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
					JCRPropertyWrapper userProperty = node.getProperty("user");

					if (userProperty != null) {
						JCRNodeWrapper replyUser = (JCRNodeWrapper) userProperty.getNode();

						// Rechercher la personne qui a posté la réponse:
						if (node != null) {
							String userID = replyUser.hasProperty("jcr:uuid") ? replyUser.getProperty("jcr:uuid").getString() : "";
							boolean isModo = getModeratorsUuids(context).contains(userID);

							// test professionnel ou modérateur et futur
							// retraité ou retraité junior
							if (!isModo
									&& (replyUser.hasProperty(PROPERTIES_USER_IS_PROFESSIONAL) && replyUser.getProperty(
											PROPERTIES_USER_IS_PROFESSIONAL).getBoolean())) {
								size++;
							}
						}
					}

				}
			}

			return size;
		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return 0;
	}

}
