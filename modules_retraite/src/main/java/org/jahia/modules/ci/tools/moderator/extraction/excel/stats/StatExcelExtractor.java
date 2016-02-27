package org.jahia.modules.ci.tools.moderator.extraction.excel.stats;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.AbstractExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IRow;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.SiteThemesExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics.SiteInscriptionsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics.SitePostsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

/**
 * This class is about to contain every possible Excel extractor used for
 * statistics. It can combine column and row in Excel file and miscellaneous
 * callback can be added in it.
 * 
 * @author foo
 * 
 */
public abstract class StatExcelExtractor extends AbstractExcelExtractor {

	transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StatsExcelAction.class);

	/**
	 * Set the title of the Excel sheet.
	 * 
	 * @param title
	 */
	public StatExcelExtractor(String title) {
		super(title);
	}

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {

		for (IColumn column : getColumns()) {
			StatColumn statColumn = (StatColumn) column;
			if (statColumn.getStatCallback() != null) {
				extractColumns(session, renderContext, statColumn);
			}
		}

		for (IRow irow : getRows()) {
			StatRow statRow = (StatRow) irow;
			if (statRow.getStatCallback() != null) {
				extractRows(session, renderContext, statRow);
			}
		}

		return null;
	}

	/**
	 * Default rows extraction treatment.
	 * 
	 * @param session
	 * @param renderContext
	 * @param statRow
	 */
	protected void extractRows(JCRSessionWrapper session, RenderContext renderContext, StatRow statRow) {
		Object data = statRow.getStatCallback().getData(session, renderContext);
		if (data != null)
			this.setStatProperty(data, statRow.getPropertyName());
	}

	/**
	 * Default columns extraction treatment.
	 * 
	 * @param session
	 * @param renderContext
	 * @param statColumn
	 */
	protected void extractColumns(JCRSessionWrapper session, RenderContext renderContext, StatColumn statColumn) {
		Object data = statColumn.getStatCallback().getData(session, renderContext);
		if (data != null)
			this.setStatProperty(data, statColumn.getPropertyName());
	}

	/**
	 * Create or use the first existing property in the list. It is the default
	 * behavior of the extract row/column part.
	 * 
	 * @param data
	 *            the data rendered in excel file
	 * @param propKey
	 *            the property key to be mapped with
	 */
	protected void setStatProperty(Object data, String propKey) {
		List<Properties> propertiesList = getProperties();
		Properties props = null;
		if (propertiesList.isEmpty()) {
			props = new Properties();
			propertiesList.add(props);
		} else
			props = propertiesList.get(0);
		props.setProperty(propKey, String.valueOf(data));
	}

	public enum StatRow implements IRow {

		SAMPLE("sample", "sample", null);

		private String label;
		private String propertyName;
		private AbstractStatCallback abstractStatCallback;

		private StatRow(String label, String propertyName, AbstractStatCallback abstractStatCallback) {
			this.label = label;
			this.propertyName = propertyName;
			this.abstractStatCallback = abstractStatCallback;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public AbstractStatCallback getStatCallback() {
			return abstractStatCallback;
		}

		public String getDefaultValue() {
			return "0";
		}

	}

	public enum StatColumn implements IColumn {

		MEMBERS_TYPE_NAME("Type de retrait" + Formatter._Character.EACUTE + "s", NAME, null), //
		MEMBERS_TYPE_VALUE("Nombre de retrait" + Formatter._Character.EACUTE + "s", VALUE, null), //

		MEMBERS_REGION_CODE("Num" + Formatter._Character.EACUTE + "ro de d" + Formatter._Character.EACUTE + "partement", "code", null), //
		MEMBERS_REGION_NAME("R" + Formatter._Character.EACUTE + "gion", NAME, null), //
		MEMBERS_REGION_VALUE("Nombre", VALUE, null), //

		MEMBERS_AGES_NAME("Tranches d'" + Formatter._Character.ACIRC + "ge", NAME, null), //
		MEMBERS_AGES_VALUE("Nombre", VALUE, null), //

		MEMBERS_ACTIVITY_TYPE("Statuts", NAME, null), //
		MEMBERS_ACTIVITY_VALUE("Nombres", VALUE, null), //

		SITE_INSCRIPTIONS_WEEK_NUMBER("Num" + Formatter._Character.EACUTE + "ro de semaine", SiteInscriptionsExtractor.WEEK, null), //
		SITE_INSCRIPTIONS_RJ("Retrait" + Formatter._Character.EACUTE + "s juniors inscrits", SiteInscriptionsExtractor.INSCRIPTIONS_RJ,
				null), //
		SITE_INSCRIPTIONS_FR("Futurs retrait" + Formatter._Character.EACUTE + "s inscrits", SiteInscriptionsExtractor.INSCRIPTIONS_FR, null), //
		SITE_INSCRIPTIONS_TOTAL("Nombre total d'inscrits", SiteInscriptionsExtractor.INSCRIPTIONS_TOTAL, null), //

		SITE_POST_WEEK_NUMBER("Num" + Formatter._Character.EACUTE + "ro de semaine", SitePostsExtractor.WEEK, null), //
		SITE_POST_QUESTIONS("Nombre de questions", SitePostsExtractor.QUESTIONS, null), //
		SITE_POST_PROS_ANSWERS("Nombre de r" + Formatter._Character.EACUTE + "ponses des pro", SitePostsExtractor.EXPERTS, null), // k
		SITE_POST_RJ_ANSWERS("Nombre de r" + Formatter._Character.EACUTE + "ponses de rj", SitePostsExtractor.RJ, null), //
		SITE_POST_FT_ANSWERS("Nombre de r" + Formatter._Character.EACUTE + "ponses de fr", SitePostsExtractor.FT, null), //
		SITE_POST_TOTAL_ANSWERS("Nombre total de r" + Formatter._Character.EACUTE + "ponses", SitePostsExtractor.TOTAL, null), //

		SITE_THEMES_LABEL("", NAME, null), //
		SITE_THEMES_QUESTIONS("Nombre de questions", SiteThemesExtractor.QUESTIONS, null), //
		SITE_THEMES_REPLIES("Nombre de réponses", SiteThemesExtractor.REPLIES, null), //
		SITE_THEMES_VIEWED("Nombre de vues", SiteThemesExtractor.VIEWED, null), //

		SITE_DOMAINS_NAME("", NAME, null), //
		SITE_DOMAINS_VALUE("Nombre de retrait" + Formatter._Character.EACUTE + "s", VALUE, null), //

		KF_NUMBERS_NAME("Type de chiffre cl" + Formatter._Character.EACUTE, NAME, null), //
		KF_NUMBERS_VALUE("Valeur", VALUE, null);

		private String label;
		private String propertyName;
		private ICallback statCallback;

		private StatColumn(String label, String propertyName, ICallback statCallback) {
			this.label = label;
			this.propertyName = propertyName;
			this.statCallback = statCallback;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public ICallback getStatCallback() {
			return statCallback;
		}

		public String getDefaultValue() {
			return "0";
		}

	}

	/**
	 * Private method for Map Extractor to populate properties. Map must contain
	 * Integer values. Entries can be any object, if not overridden,
	 * fillObjectValueProperties method interprets entry as a string.
	 * 
	 * @param session
	 * @param renderContext
	 * @param thematicsCallback
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final protected void populateWithMapCallback(JCRSessionWrapper session, RenderContext renderContext, ICallback thematicsCallback) {
		Iterator<Object> keyIterator;
		Map<Object, Object> entries;
		entries = new TreeMap((Map<Object, Object>) thematicsCallback.getData(session, renderContext));
		keyIterator = entries.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object obj = keyIterator.next();
			fillObjectValueProperties(obj, entries.get(obj));
		}
	}

	/**
	 * Default method interprets entry o as string value.
	 * 
	 * @param entry
	 * @param value
	 */
	protected void fillObjectValueProperties(Object entry, Object value) {
		Properties properties = new Properties();
		properties.put(NAME, String.valueOf(entry));
		properties.put(VALUE, String.valueOf(value));
		getProperties().add(properties);
	}

}