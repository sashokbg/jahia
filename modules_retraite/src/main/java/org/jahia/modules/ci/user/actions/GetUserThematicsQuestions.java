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
import org.jahia.services.usermanager.jcr.JCRUser;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetUserThematicsQuestions extends Action implements CiConstants {

	private final int NBR_OF_QUESTIONS = 5;

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		JCRNodeWrapper userNode = user.getNode(session);
		Long offset = GetterUtil.getLong(getParameter(parameters, "offset", "0"));

		List<JCRNodeWrapper> thematics = UserHelper.getSelectedThematics(userNode);

		if (thematics.size() == 0) {
			return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{empty:empty}"));
		}

		QueryManager queryManager = session.getWorkspace().getQueryManager();
		StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciQuestion] as question WHERE ");
		if (thematics.size() > 0) {
			query.append("( isdescendantnode(question, ['" + thematics.get(0).getPath() + "'])");
			for (int i = 1; i < thematics.size(); i++) {
				query.append(" OR isdescendantnode(question, ['" + thematics.get(i).getPath() + "']) ");
			}
			query.append(" )");
		}

		StringBuilder firstQuery = new StringBuilder(query.toString());
		StringBuilder secondQuery = new StringBuilder(query.toString());

		firstQuery.append(" AND question." + PROPERTIES_FORUM_NB_OF_REPLIES + "= '0'");
		firstQuery.append(" ORDER BY question." + PROPERTIES_FORUM_MODIFIED_DATE + " DESC");

		Query q1 = queryManager.createQuery(firstQuery.toString(), Query.JCR_SQL2);
		QueryResultWrapper queryResult1 = (QueryResultWrapper) q1.execute();
		NodeIterator iterator1 = queryResult1.getNodes();

		secondQuery.append(" AND question." + PROPERTIES_FORUM_NB_OF_REPLIES + "<> '0'");
		secondQuery.append(" ORDER BY question." + PROPERTIES_FORUM_MODIFIED_DATE + " DESC");

		Query q2 = queryManager.createQuery(secondQuery.toString(), Query.JCR_SQL2);
		QueryResultWrapper queryResult2 = (QueryResultWrapper) q2.execute();
		NodeIterator iterator2 = queryResult2.getNodes();

		long size = iterator1.getSize() + iterator2.getSize();
		JSONObject json = new JSONObject();

		if (size > 0) {
			JSONArray jsonArray = new JSONArray();
			long cptStart = offset;
			long cptEnd = offset + NBR_OF_QUESTIONS;
			while ((iterator1.hasNext() || iterator2.hasNext()) && cptEnd-- > 0) {
				JCRNodeWrapper node = null;
				if (iterator1.hasNext()) {
					node = (JCRNodeWrapper) iterator1.next();
				} else if (iterator2.hasNext()) {
					node = (JCRNodeWrapper) iterator2.next();
				} else {
					break;
				}
				if (cptStart-- > 0) {
					continue;
				}
				JSONObject question = new JSONObject();
				question.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
				question.put("shortTitle", StringUtil.cutString(StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")), 80));
				question.put("url", PageHelper.getParentOfType(node, "jnt:page").getUrl());
				question.put("createdDateWithHours", StringEscapeUtils.escapeHtml(Formatter.formatDate(
						node.getProperty(PROPERTIES_JCR_CREATED).getDate().getTime(), Formatter.FULL_DATE_FORMAT, true)));

				JCRNodeWrapper questionUser = null;
				try {
					questionUser = (JCRNodeWrapper) node.getProperty("user").getNode();
				} catch (Exception e) {
				}
				question.put("avatar", UserHelper.getUserAvatarUrl(questionUser, PROPERTIES_USER_PICTURE_AVATAR_60));

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

		if (size - offset - NBR_OF_QUESTIONS > 0) {
			json.put("offset", offset + NBR_OF_QUESTIONS);
			json.put("nbOfElments", size - offset - NBR_OF_QUESTIONS);
		}

		return new ActionResult(HttpServletResponse.SC_OK, null, json);
	}

}
