package org.jahia.modules.ci.tools.moderator.extraction.stats.periodicities;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractPeriodicityDaily implements IPeriodicity {

	static final Log LOGGER = LogFactory.getLog(AbstractPeriodicityDaily.class);

	protected Date currentDate;

	public void setPeriod(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCounter(long diff) {
		return Math.ceil(diff / 3600000L / 24L);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPeriodocityField() {
		return Calendar.DAY_OF_MONTH;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getDate(Calendar day, int i) {
		day.add(getPeriodocityField(), i);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("PERIODICITY_DAILY.getDay() : " + day.getTime());
		return day;
	}

	@Override
	public String toString() {
		return "Daily period";
	}

}