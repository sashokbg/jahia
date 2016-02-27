package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostActivThemes;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostViewedRubrics;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MostViewedThematics;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class SiteThemesExtractor extends StatExcelExtractor {

	public static final String VIEWED = "viewed";
	public static final String QUESTIONS = "q";
	public static final String REPLIES = "r";

	public SiteThemesExtractor() {
		super("");
		this.setColumns(Arrays.asList(siteThemes));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		if ("rubrics".equals(statParam)) {
			setTitle("Rubriques les plus actives");
			StatColumn.SITE_THEMES_LABEL.setLabel("Rubriques");
			fillInThemes(session, renderContext, MostActivThemes.TYPE_RUBRIC, new MostViewedRubrics());
		}
		if ("thematics".equals(statParam)) {
			setTitle("Th" + Formatter._Character.EACUTE + "matiques les plus actives");
			StatColumn.SITE_THEMES_LABEL.setLabel("Th" + Formatter._Character.EACUTE + "matiques");
			fillInThemes(session, renderContext, MostActivThemes.TYPE_THEMATIC, new MostViewedThematics());
		}
		return null;
	}

	/**
	 * @param session
	 * @param renderContext
	 */
	@SuppressWarnings("unchecked")
	protected void fillInThemes(JCRSessionWrapper session, RenderContext renderContext, String type, ICallback viewedCallback) {
		MostActivThemes activThemes = new MostActivThemes(type);

		activThemes.setPostType(MostActivThemes.POST_QUESTIONS);
		Map<String, Integer> themesQuestions = (Map<String, Integer>) activThemes.getData(session, renderContext);
		activThemes.setPostType(MostActivThemes.POST_REPLIES);
		Map<String, Integer> themesReplies = (Map<String, Integer>) activThemes.getData(session, renderContext);

		Map<String, Integer> themesViewed = (Map<String, Integer>) viewedCallback.getData(session, renderContext);
		for (String key : themesQuestions.keySet()) {
			Properties properties = new Properties();
			properties.put(NAME, key);
			properties.put(StatColumn.SITE_THEMES_QUESTIONS.getPropertyName(), String.valueOf(themesQuestions.get(key)));
			String repliesValue = "0";
			if (themesReplies.containsKey(key))
				repliesValue = String.valueOf(themesReplies.get(key));
			properties.put(StatColumn.SITE_THEMES_REPLIES.getPropertyName(), repliesValue);
			properties.put(StatColumn.SITE_THEMES_VIEWED.getPropertyName(), String.valueOf(themesViewed.get(key)));
			getProperties().add(properties);
		}
	}

	private static IColumn[] siteThemes = new IColumn[] { StatColumn.SITE_THEMES_LABEL, StatColumn.SITE_THEMES_QUESTIONS,
			StatColumn.SITE_THEMES_REPLIES, StatColumn.SITE_THEMES_VIEWED };
}