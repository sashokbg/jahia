package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.periodicities.PeriodicityExcelWeekly;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountFR;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountPro;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionAnswersCountRJ;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionQuestionCount;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class SitePostsExtractor extends StatExcelExtractor {
	public static final String WEEK = "week";
	public static final String QUESTIONS = "questions";
	public static final String EXPERTS = "experts";
	public static final String RJ = "rj";
	public static final String FT = "ft";
	public static final String TOTAL = "total";

	public SitePostsExtractor() {
		super(Formatter._Character.EACUTE + "volution des posts du forum");
		this.setColumns(Arrays.asList(sitePosts));
	}


	@SuppressWarnings("unchecked")
	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		periodicity = new PeriodicityExcelWeekly();
		Map<Date, Integer> questionDateMap = (Map<Date, Integer>) new EvolutionQuestionCount().getData(session, renderContext);
		Map<Date, Integer> answersProDateMap = (Map<Date, Integer>) new EvolutionAnswersCountPro().getData(session, renderContext);
		Map<Date, Integer> answersRJDateMap = (Map<Date, Integer>) new EvolutionAnswersCountRJ().getData(session, renderContext);
		Map<Date, Integer> answersFRDateMap = (Map<Date, Integer>) new EvolutionAnswersCountFR().getData(session, renderContext);

		List<Map<Date, Integer>> list = new ArrayList<Map<Date, Integer>>();
		list.add(answersProDateMap);
		list.add(answersRJDateMap);
		list.add(answersFRDateMap);
		addTotalMap(list);
		list.add(0, questionDateMap);

		createDataEvolution(list, this);
		return null;
	}


	private static IColumn[] sitePosts = new IColumn[] { StatColumn.SITE_POST_WEEK_NUMBER, StatColumn.SITE_POST_QUESTIONS,
			StatColumn.SITE_POST_PROS_ANSWERS, StatColumn.SITE_POST_RJ_ANSWERS, StatColumn.SITE_POST_FT_ANSWERS, StatColumn.SITE_POST_TOTAL_ANSWERS };
}