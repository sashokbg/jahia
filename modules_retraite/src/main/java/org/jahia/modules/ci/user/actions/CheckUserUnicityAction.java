package org.jahia.modules.ci.user.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author el-aarko
 * @description : permet de vérifier en mode Ajax l'unicité de l'adresse mail et
 *              le pseudoname (qui correspond au j:nodename) lors de
 *              l'inscription
 */
public class CheckUserUnicityAction extends Action implements CiConstants {

	private static List<String> reservedNames = Arrays
			.asList(new String[] { "root", "admin", "ag2r", "lamondiale",
					"ag2rlamondiale", "moderateur", "moderatrice" });
	
	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		String emailAddress = getParameter(parameters, PARAM_USER_EMAIL, "");
		String userName = getParameter(parameters, PARAM_USER_PSEUDONAME, "");
		
		userName = StringUtils.lowerCase(userName);
		emailAddress = StringUtils.lowerCase(emailAddress);
		
		JahiaUser currentUser = session.getUser();		
		JahiaUser user = null;

		JSONObject json = new JSONObject();
		try {

			if (Validator.isNotEmpty(userName) && !UserHelper.isModerator(renderContext, currentUser)) {
				if (reservedNames.contains(userName)) {
					json.put("available", false);
					return new ActionResult(HttpServletResponse.SC_OK, null, json);
				}
			}
			
			if (Validator.isNotEmpty(userName)) {				
				QueryManager queryManager = session.getWorkspace().getQueryManager();
				StringBuilder query = new StringBuilder("SELECT * FROM [jnt:user] as user WHERE lower("+PROPERTIES_USER_PSEUDODENAME+") = '"+userName+"'");
				query.append(" AND "+renderContext.getSite().getSiteKey()+"='true'");
				Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
				QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
				if (queryResult.getNodes().getSize() > 0) {
					user = userManagerService.lookupUser(((JCRNodeWrapper) queryResult.getNodes().next()).getName());
					if (user.equals(currentUser)) {
						json.put("available", true);
					} else {
						json.put("available", false);
					}
				} else {
					user = userManagerService.lookupUser(renderContext.getSite().getSiteKey()+"_"+userName);
					if (user == null) {
						json.put("available", true);
					} else {
						json.put("available", false);
					}
				}
			}

			if (Validator.isNotEmpty(emailAddress)) {
				QueryManager queryManager = session.getWorkspace().getQueryManager();
				StringBuilder query = new StringBuilder("SELECT * FROM [jnt:user] as user WHERE lower(["+PROPERTIES_USER_MAIL +"]) = '"+emailAddress+"'");
				//query.append(" AND "+renderContext.getSite().getSiteKey()+"='true'");
				Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
				QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
				if (queryResult.getNodes().getSize() > 0) {
					user = userManagerService.lookupUser(((JCRNodeWrapper) queryResult.getNodes().next()).getName());
					if (user.equals(currentUser)) {
						json.put("available", true);
					} else {
						json.put("available", false);
					}
				} else {
					json.put("available", true);
				}
			}	

		} catch (Throwable t) {
			_log.error("Erreur : " + t.getMessage(), t);
		}

		return new ActionResult(HttpServletResponse.SC_OK, null, json);

	}

	/**
	 * JahiaUserManagerService
	 */
	private JahiaUserManagerService userManagerService;

	/**
	 * Bean Property setter
	 * 
	 * @param userManagerService
	 */
	public void setUserManagerService(JahiaUserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	private transient static Logger _log = LoggerFactory.getLogger(CheckUserUnicityAction.class);

}
