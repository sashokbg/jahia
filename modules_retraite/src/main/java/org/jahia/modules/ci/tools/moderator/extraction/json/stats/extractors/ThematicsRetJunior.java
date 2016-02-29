package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import java.util.Map;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.PreferedThematicsRJ;
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
public class ThematicsRetJunior extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ThematicsRetJunior.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback callback = new PreferedThematicsRJ();

		@SuppressWarnings("unchecked")
		Map<String, Integer> thematicMap = (Map<String, Integer>) callback.getData(session, renderContext);

		try {
			JSONObject json;
			// creation du JSON de retour
			if (!thematicMap.isEmpty()) {
				for (String thematicLabel : thematicMap.keySet()) {
					Integer number = thematicMap.get(thematicLabel);
					json = new JSONObject();
					json.put("string", thematicLabel);
					json.put("number", number);
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