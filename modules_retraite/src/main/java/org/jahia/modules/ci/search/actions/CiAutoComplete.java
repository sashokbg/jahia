package org.jahia.modules.ci.search.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryParser.QueryParser;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.HtmlTools;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.query.QueryResultWrapper.RowDecorator;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.taglibs.functions.Functions;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lakreb
 * 
 */
public class CiAutoComplete extends Action {

	private static Logger logger = LoggerFactory.getLogger(CiAutoComplete.class);

	@SuppressWarnings("deprecation")
	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		QueryManager qm = session.getWorkspace().getQueryManager();

		Integer limit = Integer.parseInt(getParameter(parameters, "limit"));
		String term = getParameter(parameters, "q");

		if (term != null)
			term = Formatter.enleverAccents(term);
		else
			return new ActionResult(HttpServletResponse.SC_BAD_REQUEST);

		String termParsed = new String(term);
		termParsed = QueryParser.escape(termParsed);
		termParsed = termParsed.replaceAll("'", "\\''");

		StringBuilder queryXPath = new StringBuilder("/jcr:root");
		queryXPath.append(renderContext.getSite().getPath());
		queryXPath.append("//element(*, jmix:ciSearchable)[jcr:contains(.,'");
		queryXPath.append(termParsed);
		queryXPath.append("*')]");

		Query q = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("Preparing to execute query : " + queryXPath);
			q = qm.createQuery(queryXPath.toString(), Query.XPATH);
		} catch (InvalidQueryException e) {
			logger.error("Invalid query exception : " + queryXPath);
			logger.error(e.getCause() + " : " + e.getMessage());
		}

		List<String> list = new ArrayList<String>();
		if (q != null) {
			q.setLimit(limit);

			QueryResult queryResult = q.execute();

			RowIterator rowIterator = queryResult.getRows();

			if (logger.isDebugEnabled())
				logger.debug("Found " + rowIterator.getSize() + " result(s)...");

			while (rowIterator.hasNext()) {
				RowDecorator row = (RowDecorator) rowIterator.next();
				term = Pattern.quote(term);
				Pattern propertyMatchRegexp = Pattern.compile("\\b" + term + "\\w*\\b|" + term + "\\w*\\b", Pattern.CASE_INSENSITIVE);
				PropertyIterator stringMap = row.getNode().getProperties();
				String[] matchedProperties = new String[] { CiConstants.PROPERTIES_FORUM_POST_BODY,
						CiConstants.PROPERTIES_FORUM_POST_TITLE, CiConstants.PROPERTIES_ARTICLE_INTRO,
						CiConstants.PROPERTIES_ARTICLE_TAB_TITLTE_1, CiConstants.PROPERTIES_ARTICLE_TAB_TITLTE_2,
						CiConstants.PROPERTIES_ARTICLE_TAB_TITLTE_3 };
				while (stringMap.hasNext()) {
					JCRPropertyWrapper propertyWrapper = (JCRPropertyWrapper) stringMap.next();
					int type = propertyWrapper.getType();
					if (type == PropertyType.STRING && Arrays.asList(matchedProperties).contains(propertyWrapper.getName())) {

						String value = propertyWrapper.getValue().getString();
						String valueTagsOff = HtmlTools.removeAllHTMLComments(Functions.removeHtmlTags(value));
						String valueTagsAndAccentsOff = Formatter.enleverAccents(valueTagsOff);

						String tmpValue = new String(valueTagsAndAccentsOff);
						int offset = 0;
						int indexOf = 0;

						Matcher matcher = propertyMatchRegexp.matcher(valueTagsAndAccentsOff);

						while (matcher.find()) {
							String wordS = new String(matcher.group());
							indexOf = tmpValue.indexOf(wordS);
							offset += indexOf;
							tmpValue = new String(tmpValue.substring(indexOf + wordS.length(), tmpValue.length()));
							wordS = new String(valueTagsOff.substring(offset, offset + wordS.length())).toLowerCase();
							offset += wordS.length();
							if (!list.contains(wordS)) {
								list.add(wordS);
							}
						}
					}
				}
			}
		}

		JSONObject json = new JSONObject();
		json.put("result", list);

		return new ActionResult(HttpServletResponse.SC_OK, null, json);
	}

}
