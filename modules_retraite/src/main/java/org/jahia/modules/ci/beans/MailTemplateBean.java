package org.jahia.modules.ci.beans;

public class MailTemplateBean {
	String subject;
	String recipientMailBody;
	String senderMailBody;

	public MailTemplateBean(String subject, String recipientMailBody, String senderMailBody) {
		this.subject = subject;
		this.recipientMailBody = recipientMailBody;
		this.senderMailBody = senderMailBody;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getRecipientMailBody() {
		return recipientMailBody;
	}
	public void setRecipientMailBody(String recipientMailBody) {
		this.recipientMailBody = recipientMailBody;
	}
	public String getSenderMailBody() {
		return senderMailBody;
	}
	public void setSenderMailBody(String senderMailBody) {
		this.senderMailBody = senderMailBody;
	}
}
