package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.periodics;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.periodicities.PeriodicityJSONDaily;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.periodicities.PeriodicityJSONWeekly;

public abstract class AbstractPeriodicJSONExtractor extends AbstractExtractor {

	@Override
	public void setStatParam(String statParam) {
		if ("weekly".equals(statParam))
			periodicity = new PeriodicityJSONWeekly();
		else
			periodicity = new PeriodicityJSONDaily();

	}

}