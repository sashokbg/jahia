package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * 
 * @author baroin
 * @description : Permet de récupérer le nouveau mot de passe, de le modifier
	 * et de l'envoyer un mail au memmbre
 */

public class ChangePasswordAction extends Action implements CiConstants {

	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(ChangePasswordAction.class);

	private JahiaUserManagerService userManagerService;

	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}
	/**
	 * méthode permettant de récupérer le nouveau mot de passe
	 */
	
	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		//récupération de l'utilisateur
		JahiaUser user = session.getUser();
		
		//récupération du nouveau mot de passe
		String newPassword1= getParameter(parameters, "newPwd");
		String mail = user.getProperty(PROPERTIES_USER_MAIL);
		
		//test de ré-écriture du password
		if (Validator.isNotEmpty(newPassword1)) {
			boolean passwordChanged = user.setPassword(newPassword1);
			userManagerService.updateCache(user);
			//envoie du mail		
			if (passwordChanged) {
				if(logger.isInfoEnabled())
					logger.info("Changed new password for user : " + user);
				sendMailPassword(renderContext,req, user,newPassword1,mail );
				return  new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{OK:OK}"));
				
			} 
		}
		
		return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{KO:KO}"));
	}

	/**
	 * Méthode permettant de recevoir un mail dans lequel le nouveau password est affiché
	 * à l'inscription d'un membre
	 * @param renderContext
	 * @param parameters
	 * @param data
	 */
	public void sendMailPassword(final RenderContext renderContext,HttpServletRequest req,JahiaUser user,String newPassword,String mail ) {
		
		// Récupérer les params du mail de notification : 
		MailTemplateBean templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_CHANGE_PWD);

		String subject = templateBean.getSubject();
		String htmlBody = templateBean.getRecipientMailBody();
		
		String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_NOREPLY);
		String to = mail;
		
		String[] searchList = new String[] { MASK_MAIL_FIRSTNAME,MASK_MAIL_LASTNAME, MASK_MAIL_PASSWORD};
		String[] replacementList = new String[] { user.getProperty(PROPERTIES_USER_FIRSTNAME),user.getProperty(PROPERTIES_USER_LASTAME), newPassword};
		// Appel envoie mail de bienvenue au membre
		EmailUtil.sendMail(from, to, subject, htmlBody, searchList, replacementList, renderContext);
		
	}
}