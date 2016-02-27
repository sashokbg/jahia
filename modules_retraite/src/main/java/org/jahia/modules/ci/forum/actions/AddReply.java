package org.jahia.modules.ci.forum.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.notifications.ci.beans.MailNotificationBean;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddReply extends Action implements CiConstants {

	// private transient static Logger log = LoggerFactory.getLogger(AddReply.class);

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		JCRUser user = (JCRUser) session.getUser();
		return (ActionResult) JCRTemplate.getInstance().doExecuteWithSystemSession(user.getName(), session.getWorkspace().getName(),
				session.getLocale(), new JCRCallback<Object>() {

					public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
						JCRUser user = (JCRUser) session.getUser();
						JCRNodeWrapper userNode = user.getNode(session);
						JCRNodeWrapper questionNode = session.getNode(resource.getNode().getPath());
						String replyTitle = getParameter(parameters, "title", "RE: " + questionNode.getPropertyAsString("title"));
						String replyBody = getParameter(parameters, "body");
						String redirectTo = getParameter(parameters, "jcrRedirectTo");

						if (Validator.isNotEmpty(replyTitle) && Validator.isNotEmpty(replyBody)) {
							replyBody = Functions.escapeXml(replyBody);
							replyBody = Formatter.convertLinksToHTML(replyBody, "", true, true, 35);
							parameters.put("body", Arrays.asList(replyBody));

							JCRNodeWrapper newReplyNode = createNode(req, parameters, session.getNode(questionNode.getPath()),
									"jnt:ciReply", "", false);
							if (!Validator.isNotEmpty(newReplyNode.getPropertyAsString("title"))) {
								newReplyNode.setProperty("title", replyTitle);
							}
							newReplyNode.setProperty("user", userNode);
							if (questionNode.isNodeType("jnt:ciQuestion")) {
								questionNode.setProperty(PROPERTIES_FORUM_MODIFIED_DATE, newReplyNode.getProperty("jcr:created").getDate());
								long nbOfReplies = 1;
								if (questionNode.hasProperty("nbOfReplies")) {
									nbOfReplies += questionNode.getProperty("nbOfReplies").getLong();
								}
								questionNode.setProperty("nbOfReplies", nbOfReplies);

								if (UserHelper.isProfessional(user)) {
									long nbOfProfReplies = 1;
									if (questionNode.hasProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES)) {
										nbOfProfReplies += questionNode.getProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES).getLong();
									}
									questionNode.setProperty(PROPERTIES_FORUM_NB_OF_PROF_REPLIES, nbOfProfReplies);
								} else if (!UserHelper.isModerator(renderContext, user)) {
									ProfessionnalNotificationThread notificationThread = new ProfessionnalNotificationThread(user, questionNode, session, req, renderContext);
									notificationThread.run();
								}
							}
							session.save();
							long nbOfReplies = GetterUtil.getLong(user.getProperty(PROPERTIES_USER_NB_OF_REPLIES), 0);
							user.setProperty(PROPERTIES_USER_NB_OF_REPLIES, Long.toString(++nbOfReplies));
							String replies = GetterUtil.getString(user.getProperty(PROPERTIES_USER_REPLIES_UUID), "");
							if (!StringUtils.contains(replies, questionNode.getIdentifier())) {
								user.setProperty(PROPERTIES_USER_REPLIES_UUID, questionNode.getIdentifier() + "," + replies);
							}

						}
						// redirect to main page with order by most recent post
						// mode
						return new ActionResult(HttpServletResponse.SC_OK, redirectTo + "?orderby=lastmod", true, null);

					}

				});
	}

    private class ProfessionnalNotificationThread extends Thread {
	
	private final Logger log = LoggerFactory.getLogger(ProfessionnalNotificationThread.class);

	JCRUser user;
	JCRNodeWrapper questionNode;
	JCRSessionWrapper session;
	HttpServletRequest request;
	RenderContext renderContext;

	ProfessionnalNotificationThread(JCRUser user, JCRNodeWrapper questionNode, JCRSessionWrapper session,
		HttpServletRequest request, RenderContext renderContext) {
	    this.user = user;
	    this.questionNode = questionNode;
	    this.session = session;
	    this.request = request;
	    this.renderContext = renderContext;
	}

	@Override
	public void run() {
	    try {
		sendNotificationMailToProfessional(user, questionNode, session, request, renderContext);
	    } catch (Exception e) {
		log.error("Error on sending professionnal notification of an anwser in member topic : " );
		e.printStackTrace();
	    }
	}

	void sendNotificationMailToProfessional(JCRUser user, JCRNodeWrapper questionNode, JCRSessionWrapper session,
		HttpServletRequest request, RenderContext renderContext) throws RepositoryException, ScriptException {
	    
	    QueryManager queryManager = session.getWorkspace().getQueryManager();
	    StringBuilder query = new StringBuilder("SELECT * FROM [" + PROPERTIES_FORUM_REPLY_PRIMARY_TYPE + "] ");
	    query.append(" WHERE isdescendantnode('" + questionNode.getPath() + "')");
	    
	    Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
	    QueryResult queryResult = q.execute();
	    NodeIterator iterator = queryResult.getNodes();
	    
	    List<String> mailToTmp = new ArrayList<String>();
	    
	    while (iterator.hasNext()) {
		
		final JCRNodeWrapper n = (JCRNodeWrapper) iterator.next();
		JCRPropertyWrapper p = n.getProperty("user");
		JCRUserNode userNode = null;
		
		if (p != null)
		    userNode = (JCRUserNode) p.getNode();
		
		if (userNode != null
			&& (UserHelper.isProfessional(userNode.getDecoratedNode()) || UserHelper.isModerator(renderContext,
				userNode.getDecoratedNode()))) {
		    String toMail = userNode.getPropertyAsString(PROPERTIES_USER_MAIL);
		    Locale locale = session.getLocale();
		    Map<String, Object> bindings = new HashMap<String, Object>();
		    
		    String pseudo = "pseudo";
		    try {
			pseudo = user.getUserProperty("pseudoname").getValue();
		    } catch (Exception e) {
			log.error("Property pseudoname does not exist for user : " + user);
		    }
		    
		    bindings.put("pseudo", pseudo);
		    bindings.put("link", JCRTagUtils.getParentOfType(questionNode, "jnt:page").getAbsoluteUrl(request));
		    bindings.put("linkTitle", PageHelper.getPageTitle(JCRTagUtils.getParentOfType(questionNode, "jnt:page")));
		    bindings.put("mailSubject", "Preparonsmaretraite.fr - r\u00E9ponse suite \u00E0 votre intervention");
		    
		    String bcclist = null;
		    String template = new MailNotificationBean().getMailProfessionalReplyTemplate();
		    String ccList = null;
		    String fromMail = renderContext.getSite().getPropertyAsString(NOTIFICATION_EMAIL_NO_REPLY);
		    
		    if (!mailToTmp.contains(toMail)) {
			if (log.isInfoEnabled())
			    log.info("Notifying professional on reply added : {}, on question {}",
				    new String[] { toMail, questionNode.getPath() });
			mailToTmp.add(toMail);
			if (log.isInfoEnabled())
				log.info("Sending mail notification for mail {}, on question {} ...", new String[] {
					toMail, questionNode.getPath() });
			ServicesRegistry.getInstance().getMailService()
			.sendMessageWithTemplate(template, bindings, toMail, fromMail, ccList, bcclist, locale, CR_TEMPLATE_PACKAGE_NAME);
		    } else if (log.isDebugEnabled())
			log.debug("Do not notify professional on reply added : {}, on question {}, as it already has been...", new String[] {
				toMail, questionNode.getPath() });
		    
		}
	    }
	}
    }
	
}
