package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.params.ProcessingContext;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnsubscribeAction extends Action implements CiConstants {

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		JahiaUser currentUser = getCurrentUser();
		if (JahiaUserManagerService.isNotGuest(currentUser) && !currentUser.isRoot()
				&& !currentUser.isAdminMember(renderContext.getSite().getID())) {
			try {
				StringBuilder message = new StringBuilder();
				message.append("<html><body>L'utilisateur [");
				message.append(currentUser.getUsername());
				message.append("] s'est d&eacute;sabonn&eacute; du site <a href=\"");
				message.append(renderContext.getRequest().getScheme() + "://" + renderContext.getSite().getServerName());
				message.append("\" title=\"PMR\">Pr&eacute;parons Ma Retraite</a><br />&nbsp;&nbsp;Raison :  ");
				message.append(getParameter(parameters, PARAM_UNSUBSCRIBE_REASON));
				message.append("<br />&nbsp;&nbsp;D&eacute;tails :  <br />\"");
				message.append(getParameter(parameters, PARAM_UNSUBSCRIBE_REASON_DETAIL, ""));
				message.append("\"<br /><br />Cordialement.</body></html>");

				String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_FROM);
				String to = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_TO);

				if (to != null) {
					EmailUtil.sendMail(from, to, "D\u00e9sabonnement d'un utilisateur", message.toString(), null, null, renderContext);
				}
			} catch (Exception e) {
				LOGGER.error("Error when trying to send notification of unsubscribing of the member : " + currentUser.getName());
				e.printStackTrace();
			}

			currentUser.setProperty(PROPERTIES_USER_ACCOUNT_LOCKED, "true");
			currentUser.setProperty(PROPERTIES_USER_EMAIL_NOTIF_DIS, "true");
			currentUser.setProperty(PROPERTIES_USER_ACCEPT_NEWS, "false");
			currentUser.setProperty(PROPERTIES_USER_ACCEPT_NEWS_GROUP, "false");
			// currentUser.setProperty(PROPERTIES_USER_MAIL, "");
			// currentUser.setProperty(PROPERTIES_USER_BIRTH_DATE, "");
			// currentUser.setProperty(PROPERTIES_USER_LASTAME, "");
			// currentUser.setProperty(PROPERTIES_USER_FIRSTNAME, "");

			JahiaUser guest = userManagerService.lookupUser("guest");
			JCRSessionFactory.getInstance().setCurrentUser(guest);
			req.getSession().setAttribute(ProcessingContext.SESSION_USER, guest);
			req.getSession().setAttribute("unsubscribedOk", true);
			if (LOGGER.isInfoEnabled())
				LOGGER.info("User" + currentUser.getUsername() + " unsubscribed successfully.");
			return ActionResult.OK;

		} else {
			LOGGER.warn("Unvalid user attempted to unsubscribe : " + currentUser.getUsername());
			return new ActionResult(HttpServletResponse.SC_UNAUTHORIZED, null, new JSONObject("{KO:KO}"));
		}
	}

	/**
	 * JahiaUserManagerService
	 */
	private JahiaUserManagerService userManagerService;

	/**
	 * Bean Property setter
	 * 
	 * @param userManagerService
	 */
	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	private transient static Logger LOGGER = LoggerFactory.getLogger(UnsubscribeAction.class);

}
