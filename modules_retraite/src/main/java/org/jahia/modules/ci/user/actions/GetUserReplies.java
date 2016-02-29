package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public class GetUserReplies extends Action implements CiConstants {

	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(GetUserReplies.class);
	private final int NBR_OF_QUESTIONS = 5;

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		JahiaUser selectedUser = user;
		boolean isModerator = UserHelper.isModerator(renderContext, user);
		boolean isProfessional = UserHelper.isProfessional(user);

		String username = getParameter(parameters, "name", user.getUsername());
		if (Validator.isNotEmpty(username) && (isModerator || isProfessional)) {
			selectedUser = userManagerService.lookupUser(username);
			if (selectedUser == null) {
				return ActionResult.OK_JSON;
			}
		}

		String userReplies = selectedUser.getProperty(PROPERTIES_USER_REPLIES_UUID);
		if (StringUtils.isEmpty(userReplies)) {
			return ActionResult.OK_JSON;
		}
		StringTokenizer tokenizer = new StringTokenizer(userReplies, ",");
		long size = tokenizer.countTokens();
		JSONObject json = new JSONObject();
		Long offset = GetterUtil.getLong(getParameter(parameters, "offset", "0"));
		if (size > 0) {
			JSONArray jsonArray = new JSONArray();
			long cptStart = offset;
			long cptEnd = offset + NBR_OF_QUESTIONS;

			while (tokenizer.hasMoreTokens()) {
				if (cptStart-- > 0) {
					tokenizer.nextElement();
					continue;
				}
				if (cptEnd-- > 0) {
					String nodeUuid = (String) tokenizer.nextElement();
					JCRNodeWrapper node = null;
					try {
						node = session.getNodeByIdentifier(nodeUuid);
						JSONObject question = new JSONObject();
						question.put("questionTitle", StringEscapeUtils.escapeHtml(node.getPropertyAsString(PROPERTIES_FORUM_POST_TITLE)));
						question.put("questionBody", StringEscapeUtils.escapeHtml(node.getPropertyAsString(PROPERTIES_FORUM_POST_BODY)));
						question.put("url", PageHelper.getParentOfType(node, "jnt:page").getUrl());
						JCRNodeWrapper questionUser = null;

						try {
							questionUser = (JCRNodeWrapper) node.getProperty("user").getNode();
							question.put("status", UserHelper.getUserStatus(questionUser));
							question.put("pseudo", questionUser.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME));
						} catch (Exception e) {
							if (logger.isWarnEnabled())
								logger.warn("Unable to get user from question " + node);
						}

						question.put("avatar", UserHelper.getUserAvatarUrl(questionUser, PROPERTIES_USER_PICTURE_AVATAR_60));

						long nbOfMemberReplies = GetterUtil.getLong(node.getPropertyAsString(PROPERTIES_FORUM_NB_OF_REPLIES), 0)
								- GetterUtil.getLong(node.getPropertyAsString(PROPERTIES_FORUM_NB_OF_PROF_REPLIES), 0);
						question.put("nbOfMemberReplies", nbOfMemberReplies);

						if (nbOfMemberReplies > 1) {
							question.put("memberRepliesPlurial", "s");
						}

						long nbOfProfReplies = GetterUtil.getLong(node.getPropertyAsString(PROPERTIES_FORUM_NB_OF_PROF_REPLIES), 0);
						question.put("nbOfProfReplies", nbOfProfReplies);
						if (nbOfProfReplies > 1) {
							question.put("profRepliesPlurial", "s");
						}

						long nbOfViews = GetterUtil.getLong(node.getPropertyAsString(PROPERTIES_SITE_NB_OF_VIEWS), 0);
						question.put("nbOfViews", nbOfViews);
						if (nbOfViews > 1) {
							question.put("viewsPlurial", "s");
						}
						question.put("questionDate",
								(Formatter.formatDate(node.getProperty("jcr:created").getDate().getTime(), Formatter.SHORT_DATE_FORMAT, true)));
						question.put("questionProfileUrl", UserHelper.getProfileUrl(renderContext, questionUser));
						
						// Recuperer la derniere reponse de l'utilisateur :
						QueryManager queryManager = session.getWorkspace().getQueryManager();
						StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciReply] as reply WHERE ");
						query.append("isdescendantnode(reply, ['" + node.getPath() + "'])");
						query.append(" ORDER BY reply.[jcr:created] DESC");
						Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
						q.setLimit(1);
						QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
						NodeIterator iterator = queryResult.getNodes();
						while (iterator.hasNext()) {
							JCRNodeWrapper reply = (JCRNodeWrapper) iterator.next();
							question.put("replyBody", StringEscapeUtils.escapeHtml(reply.getPropertyAsString(PROPERTIES_FORUM_POST_BODY)));
							question.put("replyDate", StringEscapeUtils.escapeHtml(Formatter.formatDate(reply.getProperty("jcr:created")
									.getDate().getTime(), Formatter.FULL_DATE_FORMAT, true)));
						}
						jsonArray.put(question);
					} catch (ItemNotFoundException e) {
						logger.info("Item not found [" + nodeUuid + "] for user " + user);
					}
				} else {
					break;
				}
			}
			json.put("elements", jsonArray);
		}

		if (size - NBR_OF_QUESTIONS - offset > 0) {
			json.put("offset", offset + NBR_OF_QUESTIONS);
			json.put("nbOfElments", size - NBR_OF_QUESTIONS - offset);
		}

		return new ActionResult(HttpServletResponse.SC_OK, null, json);
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
}
