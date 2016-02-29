package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.periodics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountFR;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountPro;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountRJ;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionQuestionCount;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.ibm.icu.util.Calendar;

/**
 * 
 * @author foo
 * 
 */
public class QuestionsCount extends AbstractPeriodicJSONExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(QuestionsCount.class);

	@SuppressWarnings("unchecked")
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		Map<Date, Integer> questionDateMap = (Map<Date, Integer>) new EvolutionQuestionCount().getData(session, renderContext);
		Map<Date, Integer> answersFRDateMap = (Map<Date, Integer>) new EvolutionAnswersCountFR().getData(session, renderContext);
		Map<Date, Integer> answersRJDateMap = (Map<Date, Integer>) new EvolutionAnswersCountRJ().getData(session, renderContext);
		Map<Date, Integer> answersProDateMap = (Map<Date, Integer>) new EvolutionAnswersCountPro().getData(session, renderContext);

		List<Map<Date, Integer>> list = new ArrayList<Map<Date, Integer>>();
		list.add(answersFRDateMap);
		list.add(answersRJDateMap);
		list.add(answersProDateMap);

		/* if display render is a table, we must add the total column values */
		if (periodicity.getPeriodocityField() == Calendar.WEEK_OF_YEAR) {
			addTotalMap(list);
		}

		list.add(0, questionDateMap);

		try {
			createDataEvolution(list, array);
			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		return result;
	}

}