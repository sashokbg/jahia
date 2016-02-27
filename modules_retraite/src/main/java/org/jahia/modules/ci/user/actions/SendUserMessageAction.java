package org.jahia.modules.ci.user.actions;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.taglibs.standard.functions.Functions;
import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.slf4j.Logger;

/**
 * 
 * @author baroin
 * @description : Permet d'envoyer un message prive à un membre
 */

public class SendUserMessageAction extends Action implements CiConstants {

	private transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendUserMessageAction.class);

	private JahiaUserManagerService userManagerService;

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	/**
	 * methode permettant de recuperer les infos pour l'envoie du mail
	 */

	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		// recuperation du contenu du mail
		String body = GetterUtil.getString(req.getParameter("body"), "");
		String subject = GetterUtil.getString(req.getParameter("subject"), "");

		// recuperation de l'expediteur
		JCRUser memberSender = (JCRUser) getCurrentUser();
		memberSender.getUserProperties();

		// recuperation du destinataire
		String pseudoRecipient = GetterUtil.getString(req.getParameter("pseudo"), "");
		// Recherche du destinataire
		Set<Principal> set = null;
		Properties prop = new Properties();
		if (Validator.isNotNull(pseudoRecipient)) {
			prop.setProperty(PROPERTIES_USER_PSEUDODENAME, pseudoRecipient);
			set = userManagerService.searchUsers(prop);
		}
		JahiaUser memberRecipient = null;
		if (set != null && !set.isEmpty()) {
			memberRecipient = userManagerService.lookupUser(set.iterator().next().getName());
			// envoie des mails
			sendMailMessage(renderContext, memberSender, subject, body, memberRecipient);

			if (LOGGER.isInfoEnabled())
				LOGGER.info("Sent member message from [" + memberSender + "] to [" + memberRecipient + "]");
			return ActionResult.OK_JSON;
		}

		return ActionResult.INTERNAL_ERROR_JSON;
	}

	/**
	 * Methode permettant d'envoyer deux mails (1 destinatire et 1 expediteur)
	 * dans lequel le message est affiche à l'inscription d'un membre
	 * 
	 * @param renderContext
	 * @param parameters
	 * @param data
	 */
	public void sendMailMessage(final RenderContext renderContext, JCRUser memberSender, String subject, String body,
			JahiaUser memberRecipient) {

		MailTemplateBean templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_MEMBER_MESSAGE);

		/*
		 * [EVOL]#119 : Rendre les liens actifs quand on envoie un message prive
		 * 
		 * echappe les caracteres xml, rend les liens cliquables, remplace les
		 * retours chariots par des <br />
		 */
		body = Functions.escapeXml(body);
		body = Formatter.convertLinksToHTML(body, null, false, true, 100);
		body = body.replaceAll("\\r|\\n|\\rn", "<br />");

		String htmlBodySender = templateBean.getSenderMailBody();
		String htmlBodyRecipient = templateBean.getRecipientMailBody();

		// expediteur
		String toSender = memberSender.getProperty(PROPERTIES_USER_MAIL);
		String pseudo = memberSender.getProperty(PROPERTIES_USER_PSEUDODENAME);

		if (UserHelper.isModerator(renderContext, memberSender)) {
			pseudo = "le mod&eacute;rateur de Pr&eacute;parons Ma Retraite";
		}

		// ajout du message du membre
		String[] searchListSender = new String[] { MASK_MAIL_BODY };
		String[] replacementListSender = new String[] { body };

		// Recuperer les params du mail de notification :
		String fromRecipient = toSender;
		String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_NOREPLY);
		// Personnalisation du sujet du message
		// String subjectCommon = MASK_NAME_SITWEB + subject;
		String subjectCommon = templateBean.getSubject();
		// destinataire
		String toRecipient = memberRecipient.getProperty(PROPERTIES_USER_MAIL);

		// ajout du message du membre
		String[] searchListRecipient = new String[] { MASK_MAIL_BODY, MASK_MAIL_PSEUDONAME };
		String[] replacementListRecipient = new String[] { body, pseudo };

		// Appel envoie mail du destinataire
		EmailUtil.sendMail(fromRecipient, toRecipient, subjectCommon, htmlBodyRecipient, searchListRecipient, replacementListRecipient,
				renderContext);
		// Appel envoie mail de l'expediteur
		EmailUtil.sendMail(from, toSender, subjectCommon, htmlBodySender, searchListSender, replacementListSender, renderContext);

	}
}