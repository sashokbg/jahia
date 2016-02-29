package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.StringUtil;
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

public class GetUserQuestions extends Action implements CiConstants {

	private final int NBR_OF_QUESTIONS = 5;

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		JahiaUser selectedUser = user;
		boolean isModerator = UserHelper.isModerator(renderContext, user);
		boolean isProfessional = UserHelper.isProfessional(user);
		String username = getParameter(parameters, "name", user.getUsername());
		if (Validator.isNotEmpty(username) && isModerator || isProfessional) {
			selectedUser = userManagerService.lookupUser(username);
			if (selectedUser == null) {
				return ActionResult.OK_JSON;
			}
		}

		Long offset = GetterUtil.getLong(getParameter(parameters, "offset", "0"));
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciQuestion] as question WHERE ");
		query.append("question.user = '" + selectedUser.getProperty("jcr:uuid") + "'");
		query.append(" ORDER BY question." + PROPERTIES_FORUM_MODIFIED_DATE + " DESC");
		Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
		q.setOffset(offset);

		QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
		NodeIterator iterator = queryResult.getNodes();
		long size = iterator.getSize();
		JSONObject json = new JSONObject();

		if (size > 0) {
			JSONArray jsonArray = new JSONArray();
			long cpt = NBR_OF_QUESTIONS;
			while (iterator.hasNext() && cpt-- > 0) {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				JSONObject question = new JSONObject();
				question.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
				question.put("shortTitle", StringUtil.cutString(StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")), 80));
				question.put("url", PageHelper.getParentOfType(node, "jnt:page").getUrl());
				question.put("createdDateWithHours", StringEscapeUtils.escapeHtml(Formatter.formatDate(
						node.getProperty(PROPERTIES_JCR_CREATED).getDate().getTime(), Formatter.FULL_DATE_FORMAT, true)));

				JCRNodeWrapper userQuestion = null;
				try {
					userQuestion = (JCRNodeWrapper) node.getProperty("user").getNode();
				} catch (Exception e) {
				}
				question.put("avatar", UserHelper.getUserAvatarUrl(userQuestion, PROPERTIES_USER_PICTURE_AVATAR_60));

				long nbOfReplies = GetterUtil.getLong(node.getPropertyAsString(PROPERTIES_FORUM_NB_OF_REPLIES), 0);
				question.put("nbOfReplies", nbOfReplies);
				if (nbOfReplies > 1) {
					question.put("repliesPlurial", "s");
				}
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
				try {
					question.put(
							"lastModified",
							StringEscapeUtils.escapeHtml(Formatter.formatDate(node.getProperty(PROPERTIES_FORUM_MODIFIED_DATE).getDate()
									.getTime(), Formatter.SIMPLE_DATE_FORMAT, true)));
				} catch (Exception e) {
				}
				jsonArray.put(question);
			}
			json.put("elements", jsonArray);
		}

		if (size - NBR_OF_QUESTIONS > 0) {
			json.put("offset", offset + NBR_OF_QUESTIONS);
			json.put("nbOfElments", size - NBR_OF_QUESTIONS);
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
