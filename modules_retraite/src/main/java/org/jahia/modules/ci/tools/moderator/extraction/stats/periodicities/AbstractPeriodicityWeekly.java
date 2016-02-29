package org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractPeriodicityWeekly implements IPeriodicity {

	static final Log LOGGER = LogFactory.getLog(AbstractPeriodicityWeekly.class);
	/**
	 * week number are displayed in two digit
	 */
	protected NumberFormat numberFormat = NumberFormat.getInstance();

	{
		numberFormat.setMinimumIntegerDigits(2);
	}

	protected Calendar currentMondayWeek;
	protected Calendar currentSundayWeek;
	protected Calendar nextWeek;

	/**
	 * @param currentDate
	 */
	public void setPeriod(Date currentDate) {
		setCurrentMonday(currentDate);
		setCurrentSunday(currentDate);
		setNextWeek(currentDate);
	}

	/**
	 * @param currentDate
	 * @param weekOfYear
	 * @return
	 */
	private void setNextWeek(Date currentDate) {
		// setting next week day limit on Monday due to date param to
		// compare with date entry
		this.nextWeek = Calendar.getInstance();
		this.nextWeek.setTime(currentDate);
		this.nextWeek.set(Calendar.WEEK_OF_YEAR, getWeek() + 1);
	}

	private int getWeek() {
		return currentMondayWeek.get(Calendar.WEEK_OF_YEAR);
	}

	private void setCurrentMonday(Date currentDate) {
		// setting current week day limit on Monday (as getDay return)
		this.currentMondayWeek = Calendar.getInstance();
		this.currentMondayWeek.setTime(currentDate);
	}

	private void setCurrentSunday(Date currentDate) {
		// setting current week day limit on Sunday to get the good year
		this.currentSundayWeek = Calendar.getInstance();
		this.currentSundayWeek.setTime(currentDate);
		this.currentSundayWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	}

	protected abstract void addObjectCountData(int count, int mapIndex, Object... objects);

	/**
	 * Weekly comparison with dates entry in map list.
	 * 
	 * @param dateMapList
	 * @param tmpArray
	 */
	protected void fillPeriodCount(List<Map<Date, Integer>> dateMapList, Object... objects) {
		for (Map<Date, Integer> map : dateMapList) {
			Set<Date> dateSet = map.keySet();
			int count = 0;
			for (Iterator<Date> iterator = dateSet.iterator(); iterator.hasNext();) {
				Date currentDateTmp = (Date) iterator.next();
				if (currentDateTmp.before(nextWeek.getTime())
						&& (currentDateTmp.after(currentMondayWeek.getTime()) || currentDateTmp.equals(currentMondayWeek.getTime())))
					count += map.get(currentDateTmp);
			}
			addObjectCountData(count, dateMapList.indexOf(map),  objects);
		}
	}

	/**
	 * @param weekOfYear
	 * @return
	 */
	protected String getFormattedWeek() {
		return new SimpleDateFormat("yyyy").format(currentSundayWeek.getTime()) + " - S" + numberFormat.format(getWeek());
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCounter(long diff) {
		return Math.ceil(diff / 3600000L / 24L / 7L);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPeriodocityField() {
		return Calendar.WEEK_OF_YEAR;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getDate(Calendar day, int i) {
		day.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		day.add(Calendar.WEEK_OF_YEAR, i);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("PERIODICITY_WEEKLY.getDay() : " + day.getTime());
		return day;
	}

	@Override
	public String toString() {
		return "Weekly period";
	}

}