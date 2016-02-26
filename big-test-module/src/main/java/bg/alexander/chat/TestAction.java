package bg.alexander.chat;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TestAction extends Action{
	@Autowired
	private JCRTemplate jcrTemplate;
	
	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	public TestAction() {
		super.setName("testAction2");
		super.setRequireAuthenticatedUser(true);
	}
	
	@Override
	public ActionResult doExecute(HttpServletRequest session, RenderContext renderContext, Resource arg2, JCRSessionWrapper jcrSession,
			Map<String, List<String>> params, URLResolver urlResolver) throws Exception {
		
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession((s)->{System.out.println("TEST"); return ActionResult.OK;});
	}
}
