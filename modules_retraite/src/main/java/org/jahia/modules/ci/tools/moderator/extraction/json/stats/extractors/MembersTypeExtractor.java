package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
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
public class MembersTypeExtractor extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MembersTypeExtractor.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback frCallback = new KFNumberOfMembers(USER_TYPE_FUTUR_RETRAITE, ICallback.MEMBER_UNLOCKED);
		ICallback rjCallback = new KFNumberOfMembers(USER_TYPE_RETRAITE_JUNIOR, ICallback.MEMBER_UNLOCKED);

		try {
			JSONObject json = new JSONObject();
			json.put("string", "Retrait\u00E9s juniors");
			json.put("number", rjCallback.getData(session, renderContext));
			array.put(json);

			json = new JSONObject();
			json.put("string", "Futurs retrait\u00E9s");
			json.put("number", frCallback.getData(session, renderContext));
			array.put(json);

			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		return result;
	}
}