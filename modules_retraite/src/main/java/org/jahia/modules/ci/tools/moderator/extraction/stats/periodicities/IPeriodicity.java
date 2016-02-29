package org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IPeriodicity {

	/**
	 * Return the periodicity counter between min date and current date. For
	 * instance, if periodicity is weekly, the counter will be the number of
	 * weeks between the two dates. Time between the two dates must be expressed
	 * in milliseconds.
	 * 
	 * @param diff
	 *            differential time expressed in milliseconds
	 * @return the number of periodicity in differential time
	 */
	double getCounter(long diff);

	/**
	 * Returns the Calendar field to be incremented to express the periodicity.
	 * 
	 * @return a calendar field
	 */
	int getPeriodocityField();

	/**
	 * Returns the given Calendar day increment with i periodicity field.
	 * 
	 * @param day
	 *            the Calendar to increment
	 * @param i
	 *            the number of incrementation to do
	 * @return the next calendar date
	 */
	Calendar getDate(Calendar day, int i);

	/**
	 * Used to add a periodic entry/value pair. The ellipse objects is to
	 * determine according to which extractor is using the periodicity and the
	 * time period.
	 * 
	 * @param dateMapList
	 *            the data map
	 * @param currentDate
	 *            the date representing the period
	 * @param objects
	 *            every possible objects to add the periodic data in
	 */
	void addPeriodData(List<Map<Date, Integer>> dateMapList, Object... objects);

	/**
	 * Configure period according to given date.
	 * 
	 * @param currentDate
	 *            a java.util.Date
	 */
	void setPeriod(Date currentDate);

}