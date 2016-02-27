package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import java.util.Map;
import java.util.TreeMap;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.RegionsOfMembers;
import org.jahia.modules.ci.tools.moderator.extraction.stats.localities.Region;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * 
 * @author foo
 * 
 */
public class RegionExtractor extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RegionExtractor.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback callback = new RegionsOfMembers();

		@SuppressWarnings("unchecked")
		Map<Region, Integer> zipMap = new TreeMap<Region, Integer>((Map<Region, Integer>) callback.getData(session, renderContext));

		try {
			JSONArray json;
			// creation du JSON de retour
			for (Region memberRegion : zipMap.keySet()) {
				if (statParam == null || memberRegion.getFrCode().contains(statParam)) {
					Integer number = zipMap.get(memberRegion);
					json = new JSONArray();
					json.put(memberRegion.getLabel());
					json.put(number);
					array.put(json);
				}
			}
			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}
		return result;
	}

}