package org.jahia.notifications.ci.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailNotificationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String mailMemberTemplate;

	public static String mailProfessionalTemplate;

	public static String mailModeratorTemplate;

	public static String mailProfessionalReplyTemplate;

	public List<Map<String, String>> myQuestionsList = new ArrayList<Map<String, String>>();

	public Map<String, List<Map<String, String>>> newQuestionsMap = new HashMap<String, List<Map<String, String>>>();
	
	private Map<String, List<NewArticleBean>> newArticles = new HashMap <String, List<NewArticleBean>>();

	public Map<String, Map<String, List<Map<String, String>>>> newRepliesMap = new HashMap<String, Map<String, List<Map<String, String>>>>();
	
	/**
	 * @return the mailMemberTemplate
	 */
	public String getMailMemberTemplate() {
		return mailMemberTemplate;
	}

	/**
	 * @param mailMemberTemplate
	 *            the mailMemberTemplate to set
	 */
	public void setMailMemberTemplate(String mailMemberTemplate) {
		MailNotificationBean.mailMemberTemplate = mailMemberTemplate;
	}

	/**
	 * @return the mailProfessionalTemplate
	 */
	public String getMailProfessionalTemplate() {
		return mailProfessionalTemplate;
	}

	/**
	 * @param mailProfessionalTemplate
	 *            the mailProfessionalTemplate to set
	 */
	public void setMailProfessionalTemplate(String mailProfessionalTemplate) {
		MailNotificationBean.mailProfessionalTemplate = mailProfessionalTemplate;
	}

	public String getMailProfessionalReplyTemplate() {
		return mailProfessionalReplyTemplate;
	}

	public void setMailProfessionalReplyTemplate(String mailProfessionalReplyTemplate) {
		MailNotificationBean.mailProfessionalReplyTemplate = mailProfessionalReplyTemplate;
	}
	
	/**
	 * @return the myQuestionsList
	 */
	public List<Map<String, String>> getMyQuestionsList() {
		return myQuestionsList;
	}

	/**
	 * @return the newQuestionsMap
	 */
	public Map<String, List<Map<String, String>>> getNewQuestionsMap() {
		return newQuestionsMap;
	}

	/**
	 * @return the newRepliesMap
	 */
	public Map<String, Map<String, List<Map<String, String>>>> getNewRepliesMap() {
		return newRepliesMap;
	}

	/**
	 * @param newRepliesMap
	 *            the newRepliesMap to set
	 */
	public void setNewRepliesMap(Map<String, Map<String, List<Map<String, String>>>> newRepliesMap) {
		this.newRepliesMap = newRepliesMap;
	}

	/**
	 * @return the mailModeratorTemplate
	 */
	public String getMailModeratorTemplate() {
		return mailModeratorTemplate;
	}

	/**
	 * @param mailModeratorTemplate
	 *            the mailModeratorTemplate to set
	 */
	public void setMailModeratorTemplate(String mailModeratorTemplate) {
		MailNotificationBean.mailModeratorTemplate = mailModeratorTemplate;
	}

	public Map<String, List<NewArticleBean>> getNewArticles() {
		return newArticles;
	}

	public void setNewArticles(Map<String, List<NewArticleBean>> newArticles) {
		this.newArticles = newArticles;
	}


}
