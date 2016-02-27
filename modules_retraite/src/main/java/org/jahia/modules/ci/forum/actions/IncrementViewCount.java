package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;

/**
 *
 * @author : m.el-aarko
 * @description  : incrémenter le viewcount d'une page quesiton.
 */
public class IncrementViewCount extends Action {

	JCRTemplate jcrTemplate;

    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }
	
    public ActionResult doExecute(final HttpServletRequest req, RenderContext renderContext, 
    		final Resource resource, JCRSessionWrapper session, 
    		final Map<String, List<String>> parameters, URLResolver urlResolver) 
    				throws Exception {
        
    	JCRUser user = (JCRUser) session.getUser();
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(user.getName() ,session.getWorkspace().getName(),session.getLocale(),
			new JCRCallback<Object>() {
	            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
	            	String nodePath = getParameter(parameters, "nodePath");
	            	if (Validator.isNotEmpty(nodePath)) {
		            	JCRNodeWrapper node = session.getNode(nodePath);
		                // Update number of views
		            	if (!node.hasProperty("nbOfViews")) {
		            		node.addMixin("jmix:ciViewCountable");
		            		node.setProperty("nbOfViews", 0);
		            		session.save();
		            	}
		                long nbOfViews = node.getProperty("nbOfViews").getLong();
		                node.setProperty("nbOfViews", ++nbOfViews);
		                try {
		                session.save();
		                } catch (RepositoryException e) {
		                	// ignore it
		                }
	            	}
	                return ActionResult.OK_JSON;
	            }
            });
    }
}
