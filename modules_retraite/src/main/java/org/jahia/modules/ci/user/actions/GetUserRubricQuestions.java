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

public class GetUserRubricQuestions extends Action implements CiConstants {

	private final int NBR_OF_QUESTIONS = 3;

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		JCRNodeWrapper userNode = user.getNode(session);
		Long offset = GetterUtil.getLong(getParameter(parameters, "offset", "0"));

		List<JCRNodeWrapper> rubrics = UserHelper.getSelectedRubrics(userNode);

		if (rubrics.size() == 0) {
			return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{empty:empty}"));
		}

		QueryManager queryManager = session.getWorkspace().getQueryManager();
		StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciQuestion] as question WHERE ");
		if (rubrics.size() > 0) {
			query.append("isdescendantnode(question, ['" + rubrics.get(0).getPath() + "'])");
			for (int i = 1; i < rubrics.size(); i++) {
				query.append(" OR isdescendantnode(question, ['" + rubrics.get(i).getPath() + "'])");
			}
		}
		query.append(" ORDER BY " + PROPERTIES_FORUM_MODIFIED_DATE + " DESC");
		Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
		q.setOffset(offset);
		q.setLimit(NBR_OF_QUESTIONS + 1);
		QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
		NodeIterator iterator = queryResult.getNodes();
		JSONObject json = new JSONObject();
		if (iterator.getSize() > 0) {
			JSONArray jsonArray = new JSONArray();
			int cpt = NBR_OF_QUESTIONS;
			while (iterator.hasNext() && cpt-- > 0) {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				JSONObject question = new JSONObject();
				question.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
				question.put("url", PageHelper.getParentOfType(node, "jnt:page").getUrl());

				JCRNodeWrapper questionUser = null;
				try {
					questionUser = (JCRNodeWrapper) node.getProperty("user").getNode();
				} catch (Exception e) {
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
				try {
					question.put(
							"lastModified",
							StringEscapeUtils.escapeHtml(Formatter.formatDate(node.getProperty(PROPERTIES_FORUM_MODIFIED_DATE).getDate()
									.getTime(), Formatter.SIMPLE_DATE_FORMAT, true)));
				} catch (Exception e) {
					question.put("lastModified", " : Aucun");
				}
				jsonArray.put(question);
			}
			json.put("elements", jsonArray);
		}

		if (iterator.getSize() >= (NBR_OF_QUESTIONS + 1)) {
			json.put("offset", offset + NBR_OF_QUESTIONS);
		}
		return new ActionResult(HttpServletResponse.SC_OK, null, json);
	}

}
