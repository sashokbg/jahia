package org.jahia.notifications.ci.scheduler;

import org.jahia.notifications.ci.service.MailNotificationService;
import org.jahia.services.scheduler.BackgroundJob;
import org.quartz.JobExecutionContext;

/**
 * Daily mail notification. Includes members having post a question with new
 * answer or filled their preferred thematic(s), professional associated to
 * thematic(s) and moderator.
 * 
 * @author el-aarko
 * 
 */
public class MailNotificationDailyScheduler extends BackgroundJob {

	@Override
	public void executeJahiaJob(JobExecutionContext arg0) throws Exception {
		MailNotificationService.getInstance().doSendDailyMailNotifications();
	}
}
