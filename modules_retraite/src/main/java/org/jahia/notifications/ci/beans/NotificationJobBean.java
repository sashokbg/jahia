package org.jahia.notifications.ci.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jahia.services.content.JCRNodeWrapper;

public class NotificationJobBean implements Serializable {

    private static final long serialVersionUID = -3405986374990675372L;
    private boolean daily;
    private boolean everyTwoDays;
    private boolean everyFourDays;

    private boolean notificationActivated;

    private int siteID;

    private String siteAddress = "";

    private String gaSuffixExperts = "";

    private String gaSuffixMembres = "";

    private String mailTemplateLogo = "";

    private String emailNoReply = "";

    private String mailTemplateLine = "";

    private String mailTemplateBorder = "";

    private Map<String, List<JCRNodeWrapper>> questionsMap = new HashMap<String, List<JCRNodeWrapper>>();

    private Map<String, Map<String, List<JCRNodeWrapper>>> repliesMap = new HashMap<String, Map<String, List<JCRNodeWrapper>>>();

    private Map<String, List<JCRNodeWrapper>> answeredUsersMap = new HashMap<String, List<JCRNodeWrapper>>();

    private List<NewArticleBean> newArticlesList;

    private Map<String, JCRNodeWrapper> users;

    private String allThematics = "";

    private Map<String, Boolean> thematicsHavingPro = new HashMap<String, Boolean>();

    public boolean isDaily() {
        return daily;
    }

    public boolean isEveryTwoDays() {
        return everyTwoDays;
    }

    public boolean isEveryFourDays() {
        return everyFourDays;
    }

    public boolean isNotificationActivated() {
        return notificationActivated;
    }

    public int getSiteID() {
        return siteID;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public String getMailTemplateLogo() {
        return mailTemplateLogo;
    }

    public String getEmailNoReply() {
        return emailNoReply;
    }

    public String getMailTemplateLine() {
        return mailTemplateLine;
    }

    public String getMailTemplateBorder() {
        return mailTemplateBorder;
    }

    public Map<String, List<JCRNodeWrapper>> getQuestionsMap() {
        return questionsMap;
    }

    public Map<String, Map<String, List<JCRNodeWrapper>>> getRepliesMap() {
        return repliesMap;
    }

    public Map<String, List<JCRNodeWrapper>> getAnsweredUsersMap() {
        return answeredUsersMap;
    }

    public Map<String, JCRNodeWrapper> getUsers() {
        return users;
    }

    public String getAllThematics() {
        return allThematics;
    }

    public Map<String, Boolean> getThematicsHavingPro() {
        return thematicsHavingPro;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public void setEveryTwoDays(boolean everyTwoDays) {
        this.everyTwoDays = everyTwoDays;
    }

    public void setEveryFourDays(boolean everyFourDays) {
        this.everyFourDays = everyFourDays;
    }

    public void setNotificationActivated(boolean notificationActivated) {
        this.notificationActivated = notificationActivated;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public void setMailTemplateLogo(String mailTemplateLogo) {
        this.mailTemplateLogo = mailTemplateLogo;
    }

    public void setEmailNoReply(String emailNoReply) {
        this.emailNoReply = emailNoReply;
    }

    public void setMailTemplateLine(String mailTemplateLine) {
        this.mailTemplateLine = mailTemplateLine;
    }

    public void setMailTemplateBorder(String mailTemplateBorder) {
        this.mailTemplateBorder = mailTemplateBorder;
    }

    public void setQuestionsMap(Map<String, List<JCRNodeWrapper>> questionsMap) {
        this.questionsMap = questionsMap;
    }

    public void setRepliesMap(Map<String, Map<String, List<JCRNodeWrapper>>> repliesMap) {
        this.repliesMap = repliesMap;
    }

    public void setAnsweredUsersMap(Map<String, List<JCRNodeWrapper>> answeredUsersMap) {
        this.answeredUsersMap = answeredUsersMap;
    }

    public void setUsers(Map<String, JCRNodeWrapper> users) {
        this.users = users;
    }

    public void setAllThematics(String allThematics) {
        this.allThematics = allThematics;
    }

    public void setThematicsHavingPro(Map<String, Boolean> thematicsHavingPro) {
        this.thematicsHavingPro = thematicsHavingPro;
    }

    public List<NewArticleBean> getNewArticlesList() {
        return newArticlesList;
    }

    public void setNewArticlesList(List<NewArticleBean> newArticlesList) {
        this.newArticlesList = newArticlesList;
    }

    public String getGaSuffixExperts() {
        return gaSuffixExperts;
    }

    public void setGaSuffixExperts(String gaSuffixExperts) {
        this.gaSuffixExperts = gaSuffixExperts;
    }

    public String getGaSuffixMembres() {
        return gaSuffixMembres;
    }

    public void setGaSuffixMembres(String gaSuffixMembres) {
        this.gaSuffixMembres = gaSuffixMembres;
    }
}
