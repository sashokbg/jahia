package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.periodicities.PeriodicityExcelWeekly;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionMembersRegistration;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class SiteInscriptionsExtractor extends StatExcelExtractor {
	public static final String WEEK = "week";
	public static final String INSCRIPTIONS_RJ = "rj";
	public static final String INSCRIPTIONS_FR = "fr";
	public static final String INSCRIPTIONS_TOTAL = "total";

	public SiteInscriptionsExtractor() {
		super(Formatter._Character.EACUTE + "volution des inscriptions");
		this.setColumns(Arrays.asList(siteInscriptions));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		periodicity = new PeriodicityExcelWeekly();
		Map<Date, Integer> inscriptionsRJMap = (Map<Date, Integer>) new EvolutionMembersRegistration(USER_TYPE_RETRAITE_JUNIOR,
				ICallback.MEMBER_WHATEVER_THE_LOCK).getData(session, renderContext);
		Map<Date, Integer> inscriptionsFRMap = (Map<Date, Integer>) new EvolutionMembersRegistration(USER_TYPE_FUTUR_RETRAITE,
				ICallback.MEMBER_WHATEVER_THE_LOCK).getData(session, renderContext);
		Map<Date, Integer> inscriptionsTotalMap = (Map<Date, Integer>) new EvolutionMembersRegistration(null,
				ICallback.MEMBER_WHATEVER_THE_LOCK).getData(session, renderContext);

		List<Map<Date, Integer>> list = new ArrayList<Map<Date, Integer>>();
		list.add(inscriptionsRJMap);
		list.add(inscriptionsFRMap);
		list.add(inscriptionsTotalMap);

		createDataEvolution(list, this);
		return null;
	}

	private static IColumn[] siteInscriptions = new IColumn[] { StatColumn.SITE_INSCRIPTIONS_WEEK_NUMBER, StatColumn.SITE_INSCRIPTIONS_RJ,
			StatColumn.SITE_INSCRIPTIONS_FR, StatColumn.SITE_INSCRIPTIONS_TOTAL };
}