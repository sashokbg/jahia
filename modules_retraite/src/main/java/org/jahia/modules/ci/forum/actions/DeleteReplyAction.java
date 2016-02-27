package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.ForumHelper;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;


public class DeleteReplyAction extends Action implements CiConstants {

    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }
    
	JCRTemplate jcrTemplate;

	@Override
	public ActionResult doExecute(final HttpServletRequest req, RenderContext renderContext, final Resource resource, JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		JCRUser user = (JCRUser) session.getUser();
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(user.getName() ,session.getWorkspace().getName(),session.getLocale(),
			new JCRCallback<Object>() {
	            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
					JCRNodeWrapper replyNode = session.getNode(resource.getNode().getPath());
					
					if (Validator.isNotNull(replyNode)){
						ForumHelper.deletePostAndDecrementPostNumbersOf(replyNode);
					}
					
					session.save();
			        return ActionResult.OK;
	            }
            });
	}

}
