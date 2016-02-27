package org.jahia.modules.ci.tools.moderator.extraction.excel.users;

import java.util.Date;
import java.util.HashSet;
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
import org.jahia.modules.ci.helpers.ModeratorToolsHelper;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.modules.ci.tools.moderator.extraction.excel.users.UsersExcelExtractor.Column;
import org.jahia.modules.ci.tools.moderator.extraction.excel.users.UsersExcelExtractor.NewsletterGroupExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.users.UsersExcelExtractor.NewsletterSiteExtractor;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.notification.Subscription;
import org.jahia.services.notification.SubscriptionService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.utils.PaginatedList;
import org.slf4j.Logger;

public class UsersExcelAction extends Action implements CiConstants {

	private transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UsersExcelAction.class);

	private JahiaUserManagerService userManagerService;
	private SubscriptionService subscriptionService;

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		Date date = new Date();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(renderContext.getSite().getSiteKey() + " - Extracting site \"" + renderContext.getSite().getName()
					+ "\" newsletter subscribers starting ...");
		}

		// loading newsletter subscribers
		String extractorParam = getParameter(parameters, "extractor", "");

		UsersExcelExtractor extractor = null;

		try {
			final ExtractorClasses extractorClass = ExtractorClasses.valueOf(extractorParam);
			extractor = (UsersExcelExtractor) extractorClass.getExtractorClass().newInstance();
		} catch (Exception e) {
			LOGGER.error("Cannot instanciate extractor class " + extractorParam + " : " + e.getCause());

		}

		if (extractor != null) {
			extractor.getResourceNodes().add(renderContext.getSite());
			loadSubscribers(extractor);
			ModeratorToolsHelper.writeWorkbook(renderContext, date, extractor);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(renderContext.getSite().getSiteKey() + " - Extraction finished in " + (new Date().getTime() - date.getTime())
					+ "ms.");
		}
		return ActionResult.OK;
	}

	/**
	 * Load subscribers from registered users (JahiaUser) and anonymous
	 * subscribers (child nodes).
	 * 
	 * @param resourceNode
	 *            the Node to get the child newsletter component
	 * @param users
	 *            the JahiaUser List to populate
	 * @param emails
	 *            the email String List to populate
	 * @param subscriptionName
	 * 
	 * @throws RepositoryException
	 *             because of jcr nodes methods
	 */
	void loadSubscribers(UsersExcelExtractor usersExcelExtractor) throws RepositoryException {
		List<String> names = userManagerService.getUsernameList();
		
		// this set is used to resolve the issue where 2 users have the same email address
		Set<String> emailList = new HashSet<String>();
		
		for (String name : names) {
			JahiaUser user = userManagerService.lookupUser(name);
			for (String subscriptionName : usersExcelExtractor.getSubscriptionNames()) {
				if (user != null && NotificationHelper.isNotificationElligible(user, subscriptionName) && !isInMailList(user, emailList)) {
					usersExcelExtractor.getProperties().add(user.getProperties());
					emailList.add(user.getProperty(CiConstants.PROPERTIES_USER_MAIL));
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("Adding registered user " + user.getName() + " to subscriber list as " + subscriptionName
								+ " is set to true");
				}
			}
		}

		if (usersExcelExtractor.isLookInResourceNode()) {
			for (JCRNodeWrapper resourceNode : usersExcelExtractor.getResourceNodes()) {
				PaginatedList<Subscription> list = subscriptionService.getSubscriptions(resourceNode.getIdentifier(), null, false, 0, 0,
						resourceNode.getSession());
				for (Subscription subscription : list.getData()) {
					String email = subscription.getEmail();
					if (StringUtils.isNotEmpty(email) && Validator.isEmailAddress(email)) {
						Properties properties = new Properties();
						properties.putAll(subscription.getProperties());
						String property = properties.getProperty(PROPERTIES_USER_MAIL);

						// setting of cell due to old code with set mail
						// property on subscription entry
						if (StringUtils.isEmpty(property)) {
							property = properties.getProperty(NotificationHelper.J_SUBSCRIBER);
							properties.put(PROPERTIES_USER_MAIL, property);
						}
						properties.put(Column.IS_MEMBER.getPropertyName(), "false");
						properties.put(Column.IS_MODERATOR.getPropertyName(), "false");
						properties.put(Column.IS_PROFESSIONAL.getPropertyName(), "false");
						usersExcelExtractor.getProperties().add(properties);
						if (LOGGER.isDebugEnabled())
							LOGGER.debug("Adding unregistered user " + email + " to subscriber list");
					} else {
						subscriptionService.cancel(subscription.getId(), resourceNode.getSession());
					}
				}
			}
		}
	}

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	protected enum ExtractorClasses {

		SiteNewsletter(NewsletterSiteExtractor.class), //
		GroupNewsletter(NewsletterGroupExtractor.class); //

		private Class<?> extractorClass;

		public Class<?> getExtractorClass() {
			return extractorClass;
		}

		ExtractorClasses(Class<?> extractorClass) {
			this.extractorClass = extractorClass;
		}

	}
	
	private boolean isInMailList(JahiaUser user, Set<String> list){
		return list.contains(user.getProperty(CiConstants.PROPERTIES_USER_MAIL));
	}
}
