package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostActivThemes;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostViewedRubrics;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostViewedThematics;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * 
 * @author foo
 * 
 */
public class MostRubricAndThematic extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MostRubricAndThematic.class);

	@SuppressWarnings("unchecked")
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {
			MostActivThemes mostActivCallback = null;
			ICallback callbackViewed = null;

			if ("rubrics".equals(statParam)) {
				// Rubriques les plus actives
				mostActivCallback = new MostActivThemes(MostActivThemes.TYPE_RUBRIC);
				callbackViewed = new MostViewedRubrics();

			}

			if ("thematics".equals(statParam)) {
				// Thematique les plus actives
				mostActivCallback = new MostActivThemes(MostActivThemes.TYPE_THEMATIC);
				callbackViewed = new MostViewedThematics();
			}

			if (mostActivCallback != null && callbackViewed != null) {

				mostActivCallback.setPostType(MostActivThemes.POST_QUESTIONS);
				Map<String, Integer> mostActiveQuestions = (Map<String, Integer>) mostActivCallback.getData(session, renderContext);
				mostActivCallback.setPostType(MostActivThemes.POST_REPLIES);
				Map<String, Integer> mostActiveReplies = (Map<String, Integer>) mostActivCallback.getData(session, renderContext);

				Map<String, Integer> mostViewedItems = (Map<String, Integer>) callbackViewed.getData(session, renderContext);

				for (String key : mostActiveQuestions.keySet()) {
					JSONArray tmpArray = new JSONArray();
					Integer totalQuestions = mostActiveQuestions.get(key);
					Integer totalReplies = 0;
					if (mostActiveReplies.containsKey(key))
						totalReplies = mostActiveReplies.get(key);
					tmpArray.put(key);
					tmpArray.put(totalQuestions);
					tmpArray.put(totalReplies);
					tmpArray.put(mostViewedItems.get(key));
					jsonArray.put(tmpArray);

				}
			}
			result.put("rows", jsonArray);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		// retour de donnees
		return result;
	}
}