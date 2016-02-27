package org.jahia.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.NodeIteratorImpl;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.taglibs.functions.Functions;

public class SearchUtils implements CiConstants {

	private static Log log = LogFactory.getLog(SearchUtils.class);

	/**
	 * Return the first excerpt of original hit.excerpt.
	 * 
	 * @param excerpt
	 *            the text to be examined
	 * @param htmlTag
	 *            the tag to delete
	 * @return the cleaned text
	 */
	public static String extractExcerptPart(String excerpt, String searchTerm) {
		Source source = new Source(excerpt);
		StringBuilder result = new StringBuilder();

		String cleanedTS = Formatter.enleverAccents(searchTerm);
		Element excerptCatch = null;

		List<Element> spanElements = source.getAllElements("span");

		for (Element element : spanElements) {
			String text = element.getTextExtractor().toString();
			if (StringUtils.isEmpty(element.getAttributeValue("class")) && (text.contains(searchTerm) || text.contains(cleanedTS))) {
				excerptCatch = element;
				break;
			}
		}

		if (excerptCatch == null) {
			excerptCatch = spanElements.get(0);
		}

		final String replacementTerm = "#hl#";
		List<Element> highlightedElements = excerptCatch.getAllElementsByClass("searchHighlightedText");

		Segment segment = excerptCatch.getContent();
		result.append(segment.toString());

		for (Element element : highlightedElements) {
			final String escapedElement = Pattern.quote(element.toString());
			final String tmpResult = new String(result.toString());
			result = new StringBuilder(tmpResult.replaceFirst(escapedElement, replacementTerm));
		}

		Source resultSource = new Source(result.toString());
		final String text = resultSource.getTextExtractor().toString();
		result = new StringBuilder(Functions.removeHtmlTags(text));

		for (Element element : highlightedElements) {
			final String escapedElement = Pattern.quote(replacementTerm.toString());
			final String tmpResult = new String(result.toString());
			result = new StringBuilder(tmpResult.replaceFirst(escapedElement, element.toString()));
		}

		if (spanElements.size() > 2 && spanElements.indexOf(excerptCatch) < spanElements.size())
			result.append("...");

		if (excerptCatch.toString().startsWith("<span><span")) {
			Element span = excerptCatch.getAllElements("span").get(1);
			if (StringUtils.isNotEmpty(span.getAttributeValue("class")))
				result.insert(0, "...");
		}

		result.insert(0, "<div><span>");
		result.append("</span></div>");

		return result.toString();
	}

	/**
	 * Get the user list order by jcr standard metadata with system session. Due
	 * to jcr query tag bug, the order by [jcr:created] for instance, is not
	 * possible without that function.
	 * 
	 * @param userAlias
	 *            the user alias
	 * @param clauses
	 *            all clauses between "where" and the end of query
	 * @param orderBy
	 *            the order by clause
	 * @param order
	 *            the order of sort
	 * @return the NodeIterator of query result
	 * @throws RepositoryException
	 */
	public static NodeIterator getUserListOrderBy(final String userAlias, final String clauses, final String orderBy, final String order,
			final Integer limit) throws RepositoryException {
		return (NodeIterator) JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
			public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
				QueryManager queryManager = session.getWorkspace().getQueryManager();
				StringBuilder query = new StringBuilder("SELECT * FROM [jnt:user]");

				if (StringUtils.isNotEmpty(userAlias)) {
					query.append(" as ");
					query.append(userAlias);
				}

				if (StringUtils.isNotEmpty(clauses)) {
					query.append(" where ");
					query.append(clauses);
				}

				if (StringUtils.isNotEmpty(orderBy)) {
					query.append(" order by ");
					query.append(orderBy);
					if (StringUtils.isNotEmpty(order))
						query.append(" " + order);
				}

				if (log.isDebugEnabled())
					log.debug("Launching user query : " + query);

				Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
				q.setLimit(limit);
				QueryResultWrapper queryResult = null;
				int resultSize = 0;
				try {
					queryResult = (QueryResultWrapper) q.execute();
					resultSize = (int) queryResult.getNodes().getSize();
				} catch (InvalidQueryException e) {
					log.warn("Invalid query exception : " + query);
				}

				if (log.isDebugEnabled())
					log.debug("Found " + resultSize + " users.");

				if (queryResult != null)
					return queryResult.getNodes();
				return null;

			};
		});

	}

	/**
	 * Sort the user list by nbOfQuestions + nbOfReplies. Reverse list to have
	 * descendant sorting.
	 * 
	 * @param nodes
	 * @return
	 * @throws RepositoryException
	 */
	public static NodeIterator getMostActiveUsersList(final NodeIterator nodes) throws RepositoryException {
		return JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<NodeIterator>() {
			public NodeIterator doInJCR(final JCRSessionWrapper session) throws RepositoryException {

				final List<JCRNodeWrapper> users = new ArrayList<JCRNodeWrapper>();

				while (nodes.hasNext()) {
					final JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
					users.add(session.getNode(node.getPath()));
				}

				Collections.sort(users, new Comparator<JCRNodeWrapper>() {
					public int compare(JCRNodeWrapper o1, JCRNodeWrapper o2) {
						Long long1 = 0L;
						Long long2 = 0L;
						try {
							long1 = (o1.hasProperty(PROPERTIES_USER_NB_OF_QUESTIONS) ? o1.getProperty(PROPERTIES_USER_NB_OF_QUESTIONS)
									.getLong() : 0L)
									+ (o1.hasProperty(PROPERTIES_USER_NB_OF_REPLIES) ? o1.getProperty(PROPERTIES_USER_NB_OF_REPLIES)
											.getLong() : 0L);
							long2 = (o2.hasProperty(PROPERTIES_USER_NB_OF_QUESTIONS) ? o2.getProperty(PROPERTIES_USER_NB_OF_QUESTIONS)
									.getLong() : 0L)
									+ (o2.hasProperty(PROPERTIES_USER_NB_OF_REPLIES) ? o2.getProperty(PROPERTIES_USER_NB_OF_REPLIES)
											.getLong() : 0L);
						} catch (RepositoryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return long1.compareTo(long2);
					}
				});
				Collections.reverse(users);
				return new NodeIteratorImpl(users.iterator(), users.size());
			}

		});
	}
}
