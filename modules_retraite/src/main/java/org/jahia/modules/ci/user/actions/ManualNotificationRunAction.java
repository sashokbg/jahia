package org.jahia.modules.ci.user.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.notifications.ci.service.MailNotificationService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

public class ManualNotificationRunAction extends Action{

	@Override
	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		
		String job = parameters.get("job").get(0);
		
		if(job.equals("daily"))
			MailNotificationService.getInstance().doSendDailyMailNotifications();
		if(job.equals("2days"))
			MailNotificationService.getInstance().doSendEveryTwoDaysMailNotifications();
		if(job.equals("4days"))
			MailNotificationService.getInstance().doSendEveryFourDaysMailNotifications();
			
		return ActionResult.OK;
	}
}
