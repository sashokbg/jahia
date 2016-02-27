package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class MostActivThemes extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MostActivThemes.class);

	public static final String TYPE_RUBRIC = "isRubric";
	public static final String TYPE_THEMATIC = "isThematic";

	public static final Integer POST_ALL = 0;
	public static final Integer POST_QUESTIONS = 1;
	public static final Integer POST_REPLIES = 2;

	private String type = TYPE_RUBRIC;
	private int postType = POST_ALL;

	public MostActivThemes(String type, int postType) {
		this.type = type;
		this.postType = postType;
	}

	public MostActivThemes(String type) {
		this.type = type;
	}

	public MostActivThemes() {
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		ValueComparator bvc = new ValueComparator();
		Map<String, Integer> mostActiveRubrics = new HashMap<String, Integer>();
		Map<String, Integer> mostActiveRubricsOrdered = new TreeMap<String, Integer>(bvc);
		bvc.setBase(mostActiveRubrics);
		try {
			QueryResult rubricResult = executeSQLQuery(session,
					"select * from [jnt:page] as thematics where thematics." + type + "='true'", 0, 0);
			NodeIterator rubricNodes = rubricResult.getNodes();

			while (rubricNodes.hasNext()) {
				JCRNodeWrapper themeNode = (JCRNodeWrapper) rubricNodes.next();
				int size = 0;
				if (postType == POST_ALL || postType == POST_REPLIES) {
					QueryResult replyResult = executeSQLQuery(session,
							"select * from [jnt:ciReply] as reply where isdescendantnode(reply, [" + themeNode.getPath() + "])", 0, 0);
					size += replyResult.getNodes().getSize();
				}

				if (postType == POST_ALL || postType == POST_QUESTIONS) {
					QueryResult questionResult = executeSQLQuery(session,
							"select * from [jnt:ciQuestion] as question where isdescendantnode(question, [" + themeNode.getPath() + "])",
							0, 0);
					size += questionResult.getNodes().getSize();
				}
				if (TYPE_RUBRIC.equals(type))
					mostActiveRubrics.put(getPageTitle(themeNode), size);
				else if (TYPE_THEMATIC.equals(type)) {
					JCRNodeWrapper rubricPage = PageHelper.getRubricParent(themeNode);
					mostActiveRubrics.put(getThematicFullName(themeNode, rubricPage), size);
				}

			}
			mostActiveRubricsOrdered.putAll(mostActiveRubrics);

		} catch (RepositoryException e) {
			LOGGER.error("Cannot extract statistics : " + e.getMessage());
		}
		return new TreeMap<String, Integer>(mostActiveRubricsOrdered);
	}

}
