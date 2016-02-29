package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import java.util.Map;
import java.util.TreeMap;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.ZipCodeOfMembers;
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
public class ZipCodeExtractor extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ZipCodeExtractor.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback callback = new ZipCodeOfMembers();

		@SuppressWarnings("unchecked")
		Map<String, Integer> zipMap = new TreeMap<String, Integer>((Map<String, Integer>) callback.getData(session, renderContext));

		try {
			JSONObject json;
			// creation du JSON de retour
			for (String cpMember : zipMap.keySet()) {
				Integer number = zipMap.get(cpMember);
				json = new JSONObject();
				json.put("string", cpMember);
				json.put("number", number);
				array.put(json);
				if (array.length() > 9)
					break;
			}
			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}
		return result;
	}

}