package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author annosse
 * @description : permet de vérifier en mode Ajax la validité
 *              du password
 */
public class CheckCorrectPasswordAction extends Action implements CiConstants {

	
	
	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		String password = getParameter(parameters, "password", null);
		JCRUser user = (JCRUser) session.getUser();
		
		JSONObject json = new JSONObject();
		try {
			if (Validator.isNotNull(password)&& user != null && user.verifyPassword(password)) {
				json.put("correct", true);
			} else {
				json.put("correct", false);
			}

		} catch (Throwable t) {
			_log.error("Erreur : " + t.getMessage(), t);
		}

		return new ActionResult(HttpServletResponse.SC_OK, null, json);

	}

	private transient static Logger _log = LoggerFactory.getLogger(CheckCorrectPasswordAction.class);

}
