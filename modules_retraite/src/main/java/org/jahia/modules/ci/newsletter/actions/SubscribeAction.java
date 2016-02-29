package org.jahia.modules.ci.newsletter.actions;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.notification.SubscriptionService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.slf4j.Logger;

public class SubscribeAction extends Action implements CiConstants {

	private transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubscribeAction.class);

	private JahiaUserManagerService userManagerService;
	private JCRTemplate jcrTemplate;
	private SubscriptionService subscriptionService;

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			final JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(getCurrentUser().getName(), "live", new JCRCallback<Object>() {
			public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {

				String email = getParameter(parameters, PARAM_NEWSLETTER_EMAIL, "").toLowerCase();

				JCRNodeWrapper subscriberNode = null;

				if (StringUtils.isNotEmpty(email) && Validator.isEmailAddress(email)) {

					JCRUser user = null;
					Properties prop = new Properties();
					prop.put(PROPERTIES_USER_MAIL, email);
					Set<Principal> set = userManagerService.searchUsers(prop);

					if (set.size() > 1) {
						LOGGER.warn(renderContext.getSite().getSiteKey() + " - There are two users for email address : " + email);
					} else {
						if (!set.isEmpty())
							user = (JCRUser) userManagerService.lookupUser(set.iterator().next().getName());
						if (user != null && !UserHelper.isAccountLocked(user)) {
							user.setProperty(PROPERTIES_USER_ACCEPT_NEWS, "true");
							subscriberNode = user.getNode(session);
						}
					}

					if (user == null) {
						NotificationHelper.checkSubscribableIntegrity(renderContext.getSite());
						if (subscriptionService.getSubscription(renderContext.getSite(), email, session) == null) {
							Map<String, Object> properties = new HashMap<String, Object>();
							properties.put(PROPERTIES_USER_MAIL, email);
							subscriberNode = subscriptionService.subscribe(renderContext.getSite().getIdentifier(), email, properties,
									session);
						} else {
							subscriberNode = subscriptionService.getSubscription(renderContext.getSite(), email, session);
							subscriberNode.setProperty(PROPERTIES_USER_MAIL, email);
							session.save();
						}
					}
				} else {
					req.getSession().setAttribute(PARAM_NEWSLETTER_BAD_MAIL, true);
					return ActionResult.OK;
				}

				if (subscriberNode != null) {
					if (LOGGER.isInfoEnabled())
						LOGGER.info("Newsletter subscriber registered [" + subscriberNode + "]");

					final String requestWith = req.getHeader("x-requested-with");
					if (req.getHeader("accept").contains("application/json") && requestWith != null && requestWith.equals("XMLHttpRequest"))
						return ActionResult.OK_JSON;

					req.getSession().setAttribute(PARAM_NEWSLETTER_CONFIRM, true);
					return ActionResult.OK;
				} else
					return ActionResult.INTERNAL_ERROR;

			}

		});
	}

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

}
