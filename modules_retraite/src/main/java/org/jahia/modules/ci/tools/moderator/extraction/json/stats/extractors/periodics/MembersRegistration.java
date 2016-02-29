package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.periodics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions.EvolutionMembersRegistration;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.ibm.icu.util.Calendar;

/**
 * 
 * @author foo
 * 
 */
public class MembersRegistration extends AbstractPeriodicJSONExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MembersRegistration.class);

	@SuppressWarnings("unchecked")
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			ICallback allRegistrationCallback = new EvolutionMembersRegistration(null, ICallback.MEMBER_WHATEVER_THE_LOCK);
			ICallback RJRegistrationCallback = new EvolutionMembersRegistration(USER_TYPE_RETRAITE_JUNIOR,
					ICallback.MEMBER_WHATEVER_THE_LOCK);
			ICallback FRRegistrationCallback = new EvolutionMembersRegistration(USER_TYPE_FUTUR_RETRAITE,
					ICallback.MEMBER_WHATEVER_THE_LOCK);

			Map<Date, Integer> allRegistrationDateMap = (Map<Date, Integer>) allRegistrationCallback.getData(session, renderContext);

			List<Map<Date, Integer>> list = new ArrayList<Map<Date, Integer>>();

			if (periodicity.getPeriodocityField() == Calendar.WEEK_OF_YEAR) {
				Map<Date, Integer> RJRegistrationDateMap = (Map<Date, Integer>) RJRegistrationCallback.getData(session, renderContext);
				Map<Date, Integer> FRRegistrationDateMap = (Map<Date, Integer>) FRRegistrationCallback.getData(session, renderContext);
				list.add(RJRegistrationDateMap);
				list.add(FRRegistrationDateMap);
				// makeTotalColumn(list);
			}

			list.add(allRegistrationDateMap);

			createDataEvolution(list, array);

			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}
		return result;
	}
}