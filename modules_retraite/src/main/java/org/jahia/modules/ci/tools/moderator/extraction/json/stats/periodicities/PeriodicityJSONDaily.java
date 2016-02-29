package org.jahia.modules.ci.tools.moderator.extraction.json.stats.periodicities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities.AbstractPeriodicityDaily;
import org.json.JSONArray;

public class PeriodicityJSONDaily extends AbstractPeriodicityDaily {

	public void addPeriodData(List<Map<Date, Integer>> dateMapList, Object... objects) {
		JSONArray tmpArray = new JSONArray();
		tmpArray.put(new SimpleDateFormat("'new Date(\"'MM/dd/yyyy'\")'").format(this.currentDate));
		for (Map<Date, Integer> map : dateMapList) {
			tmpArray.put(map.get(this.currentDate));
		}
		((JSONArray) objects[0]).put(tmpArray);
	}

}