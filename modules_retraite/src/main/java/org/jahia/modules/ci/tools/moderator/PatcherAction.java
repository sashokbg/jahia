package org.jahia.modules.ci.tools.moderator;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.commons.util.CiConstants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class PatcherAction extends Action implements CiConstants {
	@Autowired
	private List<Patch> patchList;
	
	public List<Patch> getPatchList() {
		return patchList;
	}

	public void setPatchList(List<Patch> patchList) {
		this.patchList = patchList;
	}

	private Logger log = Logger.getLogger(PatcherAction.class);
	
	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, final URLResolver urlResolver) throws Exception {
		String workspace = "default";
		if(null != parameters.get("workspace")){
			workspace = parameters.get("workspace").get(0);
		}
		
		return (ActionResult) JCRTemplate.getInstance().doExecuteWithSystemSession(getCurrentUser().getName(), workspace, new JCRCallback<Object>() {
			public ActionResult doInJCR(JCRSessionWrapper session) throws RepositoryException {
				String listPatches = getParameter(parameters, "listPatches", "");
				String executePatchNumber = getParameter(parameters, "number", "");
				String executeAll = getParameter(parameters, "all", "");
				
				if(!StringUtils.isEmpty(listPatches) && listPatches.equals("true")){
					JSONArray jsonArray = new JSONArray();
					
					for(Patch p : patchList){
						try {
							JSONObject json = new JSONObject();
							json.put("name", p.getName());
							json.put("description", p.getDescription());
							json.put("version", p.getVersion());
							jsonArray.put(json);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					JSONObject result = new JSONObject();
					try {
						result.put("patches", jsonArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					return new ActionResult(HttpServletResponse.SC_OK, null, result);
				}
				
				if(!StringUtils.isEmpty(executePatchNumber)){
					JSONObject resultStatus = new JSONObject();
					
					try{
						int patchNumber = Integer.parseInt(executePatchNumber);
						boolean result = patchList.get(patchNumber).apply(session);
						resultStatus.put("result", result);
					}catch(NumberFormatException e){
						log.info("Trying to apply patch with wrong or non existing number");
					}catch (JSONException e) {
						e.printStackTrace();
					}
					
					return new ActionResult(HttpServletResponse.SC_OK, null, resultStatus);
				}else if(!StringUtils.isEmpty(executeAll) && executeAll.equals("true")){
					for(Patch p : patchList)
						p.apply(session);
					
					return new ActionResult(HttpServletResponse.SC_OK, null );
				}
				
				return new ActionResult(HttpServletResponse.SC_OK, null);
			}
		});
	}
}
