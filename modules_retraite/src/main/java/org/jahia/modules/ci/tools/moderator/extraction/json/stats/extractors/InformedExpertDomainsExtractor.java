package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.InformedExpertDomains;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFNumberOfMembers;
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
public class InformedExpertDomainsExtractor extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InformedExpertDomainsExtractor.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback exCallback = new InformedExpertDomains();
		ICallback totalCallback = new KFNumberOfMembers(USER_TYPE_RETRAITE_JUNIOR, ICallback.MEMBER_UNLOCKED);

		Integer exTotal = (Integer) exCallback.getData(session, renderContext);
		Long frTotal = (Long) totalCallback.getData(session, renderContext);

		try {
			JSONObject json = new JSONObject();
			json.put("string", "Renseign\u00E9s");
			json.put("number", exTotal);
			array.put(json);

			json = new JSONObject();
			json.put("string", "Non renseign\u00E9s");
			json.put("number", frTotal - exTotal);
			array.put(json);

			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		return result;
	}
}