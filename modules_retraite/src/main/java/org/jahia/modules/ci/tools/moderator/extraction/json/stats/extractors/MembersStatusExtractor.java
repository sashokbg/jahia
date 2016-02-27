package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MembersStatus;
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
public class MembersStatusExtractor extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MembersStatusExtractor.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		ICallback salarieCallback = new MembersStatus(USER_ACTIVITY_SALARIE);
		ICallback fonctionnaireCallback = new MembersStatus(USER_ACTIVITY_FONCTIONNAIRE);
		ICallback tnsCallback = new MembersStatus(USER_ACTIVITY_NON_SALARIE);
		ICallback nrCallback = new MembersStatus(null);
		try {

			final Integer salarie = (Integer) salarieCallback.getData(session, renderContext);
			final Integer fonctionnaire = (Integer) fonctionnaireCallback.getData(session, renderContext);
			final Integer tns = (Integer) tnsCallback.getData(session, renderContext);
			final Integer all = (Integer) nrCallback.getData(session, renderContext);


			JSONObject json = new JSONObject();
			json.put("string", "Retrait\u00E9(s) salari\u00E9(s)");
			json.put("number", salarie);
			array.put(json);

			json = new JSONObject();
			json.put("string", "Retrait\u00E9(s) fonctionnaire(s)");
			json.put("number", fonctionnaire);
			array.put(json);

			json = new JSONObject();
			json.put("string", "TNS");
			json.put("number", tns);
			array.put(json);

			json = new JSONObject();
			json.put("string", "Non renseign\u00E9(s)");
			json.put("number", all);
			array.put(json);

			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		return result;
	}
}