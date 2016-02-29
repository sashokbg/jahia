package org.jahia.modules.ci.user.actions;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.GetterUtil;
import org.commons.util.PwdGenerator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author el-aarko
 * @description : Permet de vérifier l'existance de l'adresse mail passé en
 *              request ensuite, elle se charge d'envoyer un mail contenant le
 *              nouveau mot de passe à l'utilisateur correspondant
 */
public class ForgottenPasswordAction extends Action implements CiConstants {

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		String mailAddress = GetterUtil.getString(req.getParameter("emailAddress"), "");
		String siteKey = renderContext.getSite().getSiteKey();

		JahiaUser user = null;

		if (Validator.isEmailAddress(mailAddress)) {
			Properties prop = new Properties();
			prop.setProperty(PROPERTIES_USER_MAIL, mailAddress);
			Set<Principal> set = userManagerService.searchUsers(prop);
			if (!set.isEmpty()) {
				Iterator<Principal> it = set.iterator();
				while (it.hasNext()) {
					user = userManagerService.lookupUser(it.next().getName());
					if (Boolean.TRUE.toString().equals(user.getProperty(renderContext.getSite().getSiteKey()))) {
						break;
					}
				}
			}
			if (user != null && UserHelper.isAccountLocked(user)) {
				if (_log.isWarnEnabled())
					_log.warn(siteKey + " - User " + mailAddress + " trying to change its password with a locked account.");
				return ActionResult.OK;
			}
		}

		// Rechercher l'utilisateur :
		if (Validator.isNotNull(user) && !user.isRoot()) {
			String userMail = user.getProperty(PROPERTIES_USER_MAIL);

			// Générer le nouveau mail :
			String newPassword = PwdGenerator.getPassword(8);
			// Modifier le mot de passe
			boolean passwordChanged = user.setPassword(newPassword);
			userManagerService.updateCache(user);

			if (passwordChanged) {

				// Récupérer les params du mail de notification :
				MailTemplateBean templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_CHANGE_PWD);

				String subject = templateBean.getSubject();
				String htmlBody = templateBean.getRecipientMailBody();

				String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_NOREPLY);
				String to = userMail;

				String[] searchList = new String[] { MASK_MAIL_FIRSTNAME,MASK_MAIL_LASTNAME, MASK_MAIL_PASSWORD };
				String[] replacementList = new String[] { user.getProperty(PROPERTIES_USER_FIRSTNAME),user.getProperty(PROPERTIES_USER_LASTAME), newPassword };
				// Appel envoie mail de bienvenue au membre
				EmailUtil.sendMail(from, to, subject, htmlBody, searchList, replacementList, renderContext);

			}
		}

		_log.info("mailAddress : " + mailAddress);

		return ActionResult.OK;
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

	private transient static Logger _log = LoggerFactory.getLogger(ForgottenPasswordAction.class);

}
