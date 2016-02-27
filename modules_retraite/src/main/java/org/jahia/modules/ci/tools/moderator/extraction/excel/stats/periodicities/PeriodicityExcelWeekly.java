package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.periodicities;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities.AbstractPeriodicityWeekly;

public class PeriodicityExcelWeekly extends AbstractPeriodicityWeekly {

	@Override
	protected void addObjectCountData(int count, int mapIndex, Object... objects) {
		Properties properties = (Properties) objects[0];
		@SuppressWarnings("unchecked")
		List<IColumn> columns = (List<IColumn>) objects[1];
		properties.put(columns.get(mapIndex + 1).getPropertyName(), String.valueOf(count));
	}

	public void addPeriodData(List<Map<Date, Integer>> dateMapList, Object... objects) {
		final IExcelExtractor extractor = (IExcelExtractor) objects[0];

		// making week entry and adding it to data objects
		Properties properties = new Properties();

		String weekKey = extractor.getColumns().get(0).getPropertyName();
		properties.put(weekKey, getFormattedWeek());

		fillPeriodCount(dateMapList, properties, extractor.getColumns());

		extractor.getProperties().add(properties);
	}
}
