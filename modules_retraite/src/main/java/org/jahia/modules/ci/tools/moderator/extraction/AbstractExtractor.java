package org.jahia.modules.ci.tools.moderator.extraction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities.IPeriodicity;

public abstract class AbstractExtractor implements IExtractor {

	public static Date minDate;
	protected String statParam;

	protected IPeriodicity periodicity;

	static {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, COMMUNITY_FIRST_DATE_DAY);
		c.set(Calendar.MONTH, COMMUNITY_FIRST_DATE_MONTH);
		c.set(Calendar.YEAR, COMMUNITY_FIRST_DATE_YEAR);
		minDate = c.getTime();
	}

	public void setStatParam(String param) {
		this.statParam = param;
	}

	/**
	 * Create object with time evolution. Used for instance on graphic
	 * "Annoted Time Line" from Google.
	 * 
	 * @param dateMapList
	 * @param periodicity
	 * @param objects
	 */
	protected void createDataEvolution(List<Map<Date, Integer>> dateMapList, Object... objects) {
		long diff = new Date().getTime() - minDate.getTime();

		List<Date> dates = new ArrayList<Date>();
		double periodicityCounter = periodicity.getCounter(diff);

		for (int i = 0; i <= periodicityCounter; i++) {
			Calendar day = Calendar.getInstance();
			day.setTime(AbstractStatCallback.simplifiedDayDate(minDate));
			day = periodicity.getDate(day, i);
			dates.add(day.getTime());
			for (Map<Date, Integer> map : dateMapList) {
				if (!map.containsKey(day.getTime()))
					map.put(day.getTime(), 0);
			}
		}

		// creation des maps de retour
		for (Date date : dates) {
			periodicity.setPeriod(date);
			periodicity.addPeriodData(dateMapList, objects);
		}
	}

	/**
	 * Add total map to the end of the list. The total is calculated with the
	 * Integer present in the existing maps. For instance
	 * <p>
	 * if date 11/11/2011 is present in map index 0 and also in map index 4, it
	 * takes the both values in respective maps and add the sum to the total
	 * map.
	 * </p>
	 * 
	 * @param list
	 */
	protected void addTotalMap(List<Map<Date, Integer>> list) {

		Map<Date, Integer> totalMap = new HashMap<Date, Integer>();
		Set<Date> tmpDates = new HashSet<Date>();

		for (Map<Date, Integer> map : list) {
			Set<Date> set = map.keySet();
			for (Iterator<Date> iterator = set.iterator(); iterator.hasNext();) {
				Date date = (Date) iterator.next();
				tmpDates.add(date);
			}
		}

		for (Iterator<Date> iterator = tmpDates.iterator(); iterator.hasNext();) {
			Date date = (Date) iterator.next();
			int count = 0;
			for (Map<Date, Integer> map : list) {
				if (map.containsKey(date))
					count += map.get(date);
			}
			totalMap.put(date, count);
		}

		list.add(totalMap);
	}
}