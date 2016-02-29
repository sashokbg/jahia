package org.jahia.notifications.ci.scheduler;

import org.jahia.notifications.ci.service.MailNotificationService;
import org.jahia.services.scheduler.BackgroundJob;
import org.quartz.JobExecutionContext;

/**
 * Every four days notifications. Includes only moderators with questions having
 * no answer since four days.
 * 
 * @author el-aarko
 * 
 */
public class MailNotificationEveryFourDaysScheduler extends BackgroundJob {

	@Override
	public void executeJahiaJob(JobExecutionContext arg0) throws Exception {
		MailNotificationService.getInstance().doSendEveryFourDaysMailNotifications();
	}
}