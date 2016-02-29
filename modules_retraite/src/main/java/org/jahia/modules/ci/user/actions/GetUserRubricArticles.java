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

public class GetUserRubricArticles extends Action implements CiConstants {

	private final int NBR_OF_ARTICLES = 5;
	
	@Override
	public ActionResult doExecute(HttpServletRequest req,
			RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters,
			URLResolver urlResolver) throws Exception {
		
		JCRUser user = (JCRUser) session.getUser();
		JCRNodeWrapper userNode = user.getNode(session);
		Long offset = GetterUtil.getLong(getParameter(parameters, "offset", "0"));
		
		List<JCRNodeWrapper> rubrics = UserHelper.getSelectedRubrics(userNode);
		
		if (rubrics.size() == 0) {
			return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{empty:empty}")); 
		}
		
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciArticle] as article WHERE ");
		if (rubrics.size() > 0) {
			query.append("isdescendantnode(article, ['"+rubrics.get(0).getPath()+"'])");
			for (int i = 1; i < rubrics.size(); i++) {
				query.append(" OR isdescendantnode(article, ['"+rubrics.get(i).getPath()+"'])");
			}
		}
		query.append(" ORDER BY [jcr:lastModified] DESC");
		Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
		q.setOffset(offset);
		q.setLimit(NBR_OF_ARTICLES+1);
		QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
		NodeIterator iterator = queryResult.getNodes();
		JSONObject json = new JSONObject();
		if (iterator.getSize() > 0) {
			JSONArray jsonArray = new JSONArray();
			int cpt = NBR_OF_ARTICLES;
			while (iterator.hasNext() && cpt-- > 0) {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				JSONObject article = new JSONObject();
				article.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
				article.put("url", PageHelper.getParentOfType(node, "jnt:page").getUrl());
				article.put("lastModified", Formatter.formatDate(node.getProperty("jcr:lastModified").getDate().getTime()));
				jsonArray.put(article);
			}
			json.put("elements", jsonArray);
		}
		
		if (iterator.getSize() >= (NBR_OF_ARTICLES+1)) {
			json.put("offset", offset+NBR_OF_ARTICLES);
		}
		return new ActionResult(HttpServletResponse.SC_OK, null, json);
	}
	
	
}
