package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.GetterUtil;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.modules.ci.helpers.NotificationHelper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;

/**
 * 
 * @author baroin
 * @description : Permet de récupérer le contenu abusif en request et d'envoyer
 *              un mail au modérateur contenant le contenu abusif
 */
public class FlagMessageAction extends Action implements CiConstants {

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		// information sur le message
		String flagMessage = GetterUtil.getString(req.getParameter("reason"), "");
		String urlMessage = GetterUtil.getString(req.getParameter("urlMessage"), "");
		// Contruction de l'URL
		/*
		 * urlMessage=
		 * "<a href="+StringPool.QUOTE+renderContext.getSite().getServerName()
		 * +urlMessage
		 * +StringPool.QUOTE+"target="+StringPool.QUOTE+"_blank"+StringPool
		 * .QUOTE+">lien vers la page</a>";
		 */

		//test de non robot
		if (flagMessage !=null && flagMessage !=""){
				String path = GetterUtil.getString(req.getParameter("path"), "");
				JCRNodeWrapper node = session.getNode(path);
				String typePost = node.getName();
				String bodyMessage = node.getPropertyAsString("body");
		
				String typeMessage;
				// test pour savoir si c'est une réponse où une question
				if (typePost.startsWith("ciReply")) {
					typeMessage = "réponse";
				} else {
					typeMessage = "question";
				}
		
				// Rechercher le membre qui a posté le message:
				JahiaUser jahiaUser = userManagerService.lookupUser(node.getCreationUser());
				String userName = null;
				
				if(jahiaUser != null)
					userName = jahiaUser.getName();
				
				// Récupérer les params du mail de notification :
		
				MailTemplateBean templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_REPORT);
		
				String subject = templateBean.getSubject();
				String htmlBody = templateBean.getRecipientMailBody();
		
				String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_FROM);
				String to = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_TO);
		
				String[] searchList = new String[] { "$typeMessage$", "$flagMessage$", "$user$", "$bodyMessage$", "$url$" };
				String[] replacementList = new String[] { typeMessage, flagMessage, userName, bodyMessage, urlMessage };
		
				if (userName != null && userName!=""){
					// Appel de la méthode de mail :
					EmailUtil.sendMail(from, to, subject, htmlBody, searchList, replacementList, renderContext);
				}
				
				
		}
		// JSONObject json = new JSONObject();
		// json.put("isFlag", true);
//		if (_log.isInfoEnabled())
//			_log.info("Flagged new post : " + node);
		
		return ActionResult.OK;
		// new ActionResult(HttpServletResponse.SC_ACCEPTED, null, json);
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

	//private transient static Logger _log = LoggerFactory.getLogger(FlagMessageAction.class);

}
