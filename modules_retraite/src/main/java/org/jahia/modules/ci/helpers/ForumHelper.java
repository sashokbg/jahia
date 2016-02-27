package org.jahia.modules.ci.helpers;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commons.util.CiConstants;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.usermanager.JahiaUser;

/**
 * 
 * @author DR. T
 * 
 */
public class ForumHelper implements CiConstants {

	
	private static final Log LOG = LogFactory.getLog(ForumHelper.class);
	
	/**
	 * Delete the post and update user or question information from this post.
	 * 
	 * @param postNode
	 *            the forum post node
	 * @throws RepositoryException
	 *             iteration over the sub nodes can throw RepositoryException
	 */
	public static void deletePostAndDecrementPostNumbersOf(JCRNodeWrapper postNode) throws RepositoryException {

		JCRNodeWrapper userNode = null;
		try {
			final JCRPropertyWrapper userPropertyWrapper = postNode.getProperty("user");
			userNode = (JCRNodeWrapper) userPropertyWrapper.getNode();
		} catch (Exception e) {
			LOG.warn("A User has been removed.");
		}
		
		final String postType = postNode.getPrimaryNodeTypeName();

		long nbOfUserTypePosts = 1;
		String propertyToDecrement = PROPERTIES_USER_NB_OF_REPLIES;

		if (postType.equals(PROPERTIES_FORUM_REPLY_PRIMARY_TYPE) || postType.equals(PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE)) {

			if (postType.equals(PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE)) {
				NodeIterator nodeIterator = postNode.getNodes();
				while (nodeIterator.hasNext()) {
					JCRNodeWrapper replyPost = (JCRNodeWrapper) nodeIterator.next();
					deletePostAndDecrementPostNumbersOf(replyPost);
				}
				propertyToDecrement = PROPERTIES_USER_NB_OF_QUESTIONS;
			} else {
				JCRNodeWrapper questionNode = postNode.getParent();
				if (userNode != null && UserHelper.isProfessional(userNode)) {
					long nbOfProfReplies = 1;
					if (questionNode.hasProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES)) {
						nbOfProfReplies = questionNode.getProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES).getLong();
					}
					questionNode.setProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES, nbOfProfReplies - 1);
				}

				long nbOfReplies = 1;
				if (questionNode.hasProperty("nbOfReplies")) {
					nbOfReplies = questionNode.getProperty("nbOfReplies").getLong();
				}
				questionNode.setProperty("nbOfReplies", nbOfReplies - 1);
			}

			if (userNode != null) {
				// decrementer
				if (userNode.hasProperty(propertyToDecrement) && userNode.getProperty(propertyToDecrement).getLong() > 1) {
					nbOfUserTypePosts = userNode.getProperty(propertyToDecrement).getLong();
				}
				nbOfUserTypePosts--;
				JahiaUser userToUpdate = ServicesRegistry.getInstance().getJahiaUserManagerService().lookupUser(userNode.getName());
				userToUpdate.setProperty(propertyToDecrement, Long.toString(nbOfUserTypePosts));
				
				// supprimer l'uuid de la reponse :
				if (postType.equals(PROPERTIES_FORUM_REPLY_PRIMARY_TYPE)) {
					String userReplies = userToUpdate.getProperty(PROPERTIES_USER_REPLIES_UUID);
					userReplies = StringUtils.remove(userReplies, postNode.getIdentifier());
					userReplies = StringUtils.join(StringUtils.split(userReplies, ","));
					userToUpdate.setProperty(PROPERTIES_USER_REPLIES_UUID, userReplies);
				}
			}
			
			postNode.remove();
		}
	}
}
