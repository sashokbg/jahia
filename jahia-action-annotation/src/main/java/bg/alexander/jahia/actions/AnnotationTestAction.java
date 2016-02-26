package bg.alexander.jahia.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import static org.jahia.bin.ActionResult.*;

import bg.alexander.jahia.JahiaAction;

@JahiaAction
public class AnnotationTestAction extends Action{

	public AnnotationTestAction() {
		int i = 5;
	}
	
	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		System.out.println("Action");
		
		return OK;
	}
}
