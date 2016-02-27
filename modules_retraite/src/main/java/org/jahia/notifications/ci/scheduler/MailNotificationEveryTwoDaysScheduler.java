package org.jahia.notifications.ci.scheduler;

import org.jahia.notifications.ci.service.MailNotificationService;
import org.jahia.services.scheduler.BackgroundJob;
import org.quartz.JobExecutionContext;

/**
 * Every two days notifications. Includes professionals and moderators with
 * questions having no answer since two days.
 * 
 * @author el-aarko
 * 
 */
public class MailNotificationEveryTwoDaysScheduler extends BackgroundJob {

	@Override
	public void executeJahiaJob(JobExecutionContext arg0) throws Exception {
		MailNotificationService.getInstance().doSendEveryTwoDaysMailNotifications();
	}
}
