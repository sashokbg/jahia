package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.StringUtil;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.taglibs.functions.Functions;
import org.jahia.taglibs.jcr.node.JCRTagUtils;

/**
 * 
 * @author baroin
 * @description : Permet de récupérer le nouveau mot de passe, de le modifier et
 *              de l'envoyer un mail au membre
 */

public class SendUserFriendlyMessageAction extends Action implements CiConstants {

	// private transient static Logger LOGGER =
	// org.slf4j.LoggerFactory.getLogger(SendUserFriendlyMessageAction.class);

	private JahiaUserManagerService userManagerService;

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	/**
	 * méthode permettant de récupérer le nouveau mot de passe
	 */

	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		// récupération du contenu du mail
		String path = GetterUtil.getString(req.getParameter("path"), "");
		String url = GetterUtil.getString(req.getParameter("url"), "");
		String emailFriend = GetterUtil.getString(req.getParameter("emailFriend"), "");
		// String uuid=GetterUtil.getString(req.getParameter("uuid"), "");
		String type1 = GetterUtil.getString(req.getParameter("type"), "");
		String type = "";

		// récupération de l'expéditeur
		String senderName = StringEscapeUtils.escapeHtml(GetterUtil.getString(req.getParameter("senderName"), ""));
		String senderEmail = GetterUtil.getString(req.getParameter("senderEmail"), "");
		// JCRUser memberSender = (JCRUser) getCurrentUser();
		// memberSender.getUserProperties();

		if (!GenericValidator.isEmail(senderEmail) || !GenericValidator.isEmail(emailFriend))
			return null;

		JCRNodeWrapper node = session.getNode(path);
		MailTemplateBean templateBean;
		String[] searchListRecipient;
		String[] replacementListRecipient;
		// JCRNodeWrapper node = session.getNodeByUUID(uuid);

		// Pour une réponse
		if (type1.equals("reponse")) {
			type = "la r" + Formatter._Character.EACUTE + "ponse " + Formatter._Character.AGRAVE + " la question suivante:";
			String bodyReponse = StringUtil.cutString(
					StringEscapeUtils.escapeHtml(Functions.removeHtmlTags(node.getPropertyAsString("body"))), 100);
			templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_MEMBER_FRIENDLY_MESSAGE_QUESTION);

			// Rechercher la personne qui a posté la réponse:
			JahiaUser replyUser = userManagerService.lookupUser(node.getCreationUser());
			// test si expert ou membre
			String statutReply = "";
			String nameReply = "";
			String isModo = null;
			isModo = replyUser.getProperty(PROPERTIES_USER_IS_MODERATOR);
			if ("true".equals(replyUser.getProperty(PROPERTIES_USER_IS_PROFESSIONAL)) || "true".equals(isModo)) {
				statutReply = replyUser.getProperty("j:function");
				nameReply = replyUser.getProperty(PROPERTIES_USER_FIRSTNAME) + " " + replyUser.getProperty(PROPERTIES_USER_LASTAME);
				if (statutReply == null || statutReply.equals("")) {
					statutReply = "mod" + Formatter._Character.EACUTE + "rateur";
				}

			} else {
				nameReply = replyUser.getProperty(PROPERTIES_USER_PSEUDODENAME);
				statutReply = replyUser.getProperty("userType");
				if (statutReply.equals("radFuturRetraite")) {
					statutReply = "Futur retrait" + Formatter._Character.EACUTE;
				} else {
					statutReply = "Retrait" + Formatter._Character.EACUTE + " junior";
				}
			}

			addRecommandableThing(node);

			// Récupération de la question et de son auteur
			JCRNodeWrapper nodeQuestion = JCRTagUtils.getParentOfType(node, "jnt:ciQuestion");
			String title = StringEscapeUtils.escapeHtml(nodeQuestion.getPropertyAsString("title"));
			JahiaUser QuestionUser = userManagerService.lookupUser(nodeQuestion.getCreationUser());
			String nameQuestion = QuestionUser.getProperty(PROPERTIES_USER_PSEUDODENAME);
			String statutQuestion = replyUser.getProperty("userType");
			if (statutQuestion.equals("radFuturRetraite")) {
				statutQuestion = "Futur retrait" + Formatter._Character.EACUTE;
			} else {
				statutQuestion = "Retrait" + Formatter._Character.EACUTE + " junior";
			}
			searchListRecipient = new String[] { MASK_MAIL_TYPE, MASK_MAIL_TITLE, MASK_MAIL_BODY, MASK_MAIL_URL, "$nameQuestion$",
					"$statutQuestion$", "$nameReply$", "$statutReply$", "$firstname$" };
			replacementListRecipient = new String[] { type, title, bodyReponse, url, nameQuestion, statutQuestion, nameReply, statutReply,
					senderName };

		}
		// pour un article
		else {
			type = "l" + Formatter._Character.APOS + "article suivant:";
			// Récupération du titre et du chapeau
			JCRNodeWrapper nodeArticle = JCRTagUtils.getMeAndParentsOfType(node, "jnt:ciArticle").get(0);
			String title = StringEscapeUtils.escapeHtml(nodeArticle.getPropertyAsString("title"));
			String intro = StringUtil.cutString((nodeArticle.getPropertyAsString("intro")), 100);
			addRecommandableThing(nodeArticle);
			searchListRecipient = new String[] { MASK_MAIL_TYPE, MASK_MAIL_TITLE, MASK_MAIL_INTRO, MASK_MAIL_URL, "$firstname$" };
			replacementListRecipient = new String[] { type, title, intro, url, senderName };
			templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_MEMBER_FRIENDLY_MESSAGE_ARTICLE);
		}

		// envoie de mail
		sendMailMessage(renderContext, senderEmail, senderName, emailFriend, templateBean, searchListRecipient, replacementListRecipient);

		// if (LOGGER.isInfoEnabled())
		// LOGGER.info("Sent member message from [" + memberSender + "]");

		return ActionResult.OK_JSON;
		// return ActionResult.OK;
	}

	private void addRecommandableThing(final JCRNodeWrapper node) throws RepositoryException {
		JCRTemplate.getInstance().doExecuteWithSystemSession("guest", "live", new JCRCallback<Object>() {
			public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
				JCRNodeWrapper systemNode = session.getNode(node.getPath());
				if (!systemNode.isNodeType("jmix:ciRecommandable")) {
					systemNode.addMixin("jmix:ciRecommandable");
					systemNode.saveSession();
				}

				if (!systemNode.hasProperty("recommandedScore"))
					systemNode.setProperty("recommandedScore", 0L);

				long counter = systemNode.getProperty("recommandedScore").getLong();
				counter++;
				systemNode.setProperty("recommandedScore", counter);
				systemNode.saveSession();
				return null;
			}
		});
	}

	/**
	 * Méthode permettant d'envoyer un mail
	 * 
	 * @param renderContext
	 * @param parameters
	 * @param data
	 */
	public void sendMailMessage(final RenderContext renderContext, String senderEmail, String senderName, String emailFriend,
			MailTemplateBean templateBean, String[] searchListRecipient, String[] replacementListRecipient) {

		String htmlBody = templateBean.getRecipientMailBody();
		String subject = templateBean.getSubject();
		StringUtils.replaceOnce(subject, "$firstname$", senderName);

		// expéditeur
		String fromRecipient = senderEmail;
		// ajout du message du membre
		// destinataire
		String toRecipient = emailFriend;

		// Appel envoie mail du destinataire
		EmailUtil.sendMail(fromRecipient, toRecipient, "preparonsmaretraite@gmail.com", subject, htmlBody, searchListRecipient,
				replacementListRecipient, renderContext);

	}
}