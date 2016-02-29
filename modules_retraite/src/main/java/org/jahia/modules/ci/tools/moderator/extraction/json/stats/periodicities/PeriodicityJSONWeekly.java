package org.jahia.modules.ci.tools.moderator.extraction.json.stats.periodicities;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities.AbstractPeriodicityWeekly;
import org.json.JSONArray;

public class PeriodicityJSONWeekly extends AbstractPeriodicityWeekly {

	/**
	 * @param tmpArray
	 * @param count
	 */
	protected void addObjectCountData(int count, int mapIndex, Object... objects) {
		((JSONArray) objects[0]).put(count);
	}

	public void addPeriodData(List<Map<Date, Integer>> dateMapList, Object... objects) {

		// making week entry and adding it to data array
		JSONArray tmpArray = new JSONArray();
		tmpArray.put(getFormattedWeek());

		fillPeriodCount(dateMapList, tmpArray);
		((JSONArray) objects[0]).put(tmpArray);

	}

}