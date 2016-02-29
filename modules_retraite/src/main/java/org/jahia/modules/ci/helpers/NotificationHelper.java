package org.jahia.modules.ci.helpers;


import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaUser;

public class NotificationHelper implements CiConstants {

	public static final String J_SUBSCRIPTIONS = "j:subscriptions";
	public static final String JMIX_SUBSCRIBABLE = "jmix:subscribable";
	public static final String J_SUBSCRIBER = "j:subscriber";

	static final Log LOG = LogFactory.getLog(NotificationHelper.class);

	/**
	 * Check if user can receive a notification type.
	 * 
	 * @param user
	 *            the JahiaUser to check for
	 * @param notificationPropertyName
	 *            the name of the notification property
	 * @return true if the user can receive the notification
	 */
	public static boolean isNotificationElligible(JahiaUser user, String notificationPropertyName) {
		// verify if user notification is set to true, user account is
		// not locked, it has an email, ...
		String email = user.getProperty(PROPERTIES_USER_MAIL);
		boolean isAccountLocked = Boolean.parseBoolean(user.getProperty(PROPERTIES_USER_ACCOUNT_LOCKED));
		boolean isNotificationEnabled = Boolean.parseBoolean(user.getProperty(notificationPropertyName));
		return StringUtils.isNotEmpty(email) && Validator.isEmailAddress(email) && !isAccountLocked && isNotificationEnabled;
	}

	/**
	 * Check if the can receive subscription nodes and make it able if not.
	 * 
	 * @param siteNode
	 * @throws RepositoryException
	 */
	public static void checkSubscribableIntegrity(final JCRSiteNode siteNode) throws RepositoryException {
		if (siteNode != null) {
			if (!siteNode.isNodeType(JMIX_SUBSCRIBABLE)) {
				LOG.info("Going to add mixin on site node : " + siteNode);
				JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<Object>() {
					public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
						session.getNodeByIdentifier(siteNode.getIdentifier()).addMixin(JMIX_SUBSCRIBABLE);
						session.save();
						return null;
					};
				});
			}
		}
	}

	public static MailTemplateBean getMailTemplate(RenderContext renderContext, String target) {
		try {
			JCRPropertyWrapper propertyWrapper = renderContext.getSite().getProperty(target);
			if (propertyWrapper != null) {
				JCRNodeWrapper templateNode = (JCRNodeWrapper) propertyWrapper.getNode();
				return new MailTemplateBean(templateNode.getPropertyAsString(PROPERTIES_MAIL_TEMPLATE_SUBJECT),
						templateNode.getPropertyAsString(PROPERTIES_MAIL_TEMPLATE_RECIPIENT_BODY),
						templateNode.getPropertyAsString(PROPERTIES_MAIL_TEMPLATE_SENDER_BODY));
			}
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
