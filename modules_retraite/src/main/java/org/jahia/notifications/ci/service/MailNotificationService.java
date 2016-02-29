package org.jahia.notifications.ci.service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.StringUtil;
import org.jahia.exceptions.JahiaException;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.notifications.ci.beans.MailNotificationBean;
import org.jahia.notifications.ci.beans.NewArticleBean;
import org.jahia.notifications.ci.beans.NotificationJobBean;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerRoutingService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.jahia.taglibs.functions.Functions;
import org.jahia.utils.JahiaTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send mail according to notification job. First verifies site's parameters so
 * that to know if mail notification is activated on it, and others like logo,
 * mail stylistics element, and site and no-reply address.
 * <p>
 * (1) sets posts map involved in notifications, meaning either it is a question
 * having never been answered (only for moderator and professional
 * notification), or new reply of the day or new question of the day or again
 * new answer of user questions
 * <p>
 * (2) sets... to be continued
 *
 * @author el-aarko
 * @author lakreb (refactorer)
 */
public class MailNotificationService implements CiConstants {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MailNotificationService.class);
    private static MailNotificationService NOTIFICATION_SERVICE;

    /**
     * date formatter of answered question professional notification
     */
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy' - 'HH'H'mm");

    /**
     * private singleton constructor
     */
    private MailNotificationService() {

    }

    /**
     * init method
     *
     * @return a singleton instance of notification service
     */
    public static MailNotificationService getInstance() {
        if (NOTIFICATION_SERVICE == null)
            NOTIFICATION_SERVICE = new MailNotificationService();
        return NOTIFICATION_SERVICE;
    }

    public void doSendDailyMailNotifications() throws RepositoryException, JahiaException {
        final Iterator<JahiaSite> jahiaSites = ServicesRegistry.getInstance().getJahiaSitesService().getSites();
        JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", Locale.FRENCH, new JCRCallback<Object>() {
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                while (jahiaSites.hasNext()) {
                    JahiaSite jahiaSite = jahiaSites.next();
                    NotificationJobBean jobBean = new NotificationJobBean();

                    try {
                        initMailTemplateContributedElements(jahiaSite, jobBean);
                    } catch (JahiaException e) {
                        logger.error("doSendDailyMailNotifications - Error when trying to init job bean");
                    }

                    if (jobBean.isNotificationActivated()) {
                        Date startDate = new Date();

                        if (logger.isInfoEnabled()) {
                            logger.info("Starting daily notification on site \"" + jahiaSite.getTitle() + "\" [id=" + jobBean.getSiteID()
                                    + "]");
                        }

                        // specifying job type
                        jobBean.setDaily(true);

                        // uniquement pour les membres (posteur de question)
                        prepareAnsweredUsersMap(session, jobBean);

						/*
                         * commun a tous les membres (questions triees par
						 * thematiques)
						 */
                        prepareNewQuestionsMap(session, jobBean);

						/*
                         * preparations des nouvelles reponses du jours,
						 * uniquement pour les moderateurs/professionnels.
						 */
                        prepareNewRepliesMap(session, jobBean);

                        prepareNewArticlesMap(session, jobBean);

                        // commun a tous les membres
                        prepareUsersToNotify(session, jobBean);

						/*
                         * prepare et envoie le mail de notification pour chaque
						 * type de membre (retraite, pro, modo)
						 */
                        sendMails(session, jobBean);

                        if (logger.isInfoEnabled()) {
                            Date endDate = new Date();
                            logger.info("Daily notification on site \"" + jahiaSite.getTitle() + "\" [id=" + jobBean.getSiteID()
                                    + "] executed in : " + (endDate.getTime() - startDate.getTime()) + "ms.");
                        }
                    } else if (logger.isDebugEnabled())
                        logger.debug("La notification quotidienne est d" + Formatter._Character.EACUTE + "sactiv"
                                + Formatter._Character.EACUTE + "e sur le site : " + jahiaSite.getSiteKey());
                }
                return null;
            }
        });
    }

    public void doSendEveryTwoDaysMailNotifications() throws RepositoryException, JahiaException {
        final Iterator<JahiaSite> jahiaSites = ServicesRegistry.getInstance().getJahiaSitesService().getSites();
        JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", Locale.FRENCH, new JCRCallback<Object>() {
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                while (jahiaSites.hasNext()) {
                    JahiaSite jahiaSite = jahiaSites.next();
                    NotificationJobBean jobBean = new NotificationJobBean();
                    try {
                        initMailTemplateContributedElements(jahiaSite, jobBean);
                    } catch (JahiaException e) {
                        logger.error("doSendDailyMailNotifications - Error when trying to init job bean");
                    }
                    if (jobBean.isNotificationActivated()) {
                        Date startDate = new Date();
                        if (logger.isInfoEnabled())
                            logger.info("Starting two days notification on site \"" + jahiaSite.getTitle() + "\" [id="
                                    + jobBean.getSiteID() + "]");
                        jobBean.setEveryTwoDays(true);
                        prepareNewQuestionsMap(session, jobBean);
                        prepareUsersToNotify(session, jobBean);
                        sendMails(session, jobBean);
                        if (logger.isInfoEnabled()) {
                            Date endDate = new Date();
                            logger.info("Two days notification on site \"" + jahiaSite.getTitle() + "\" [id=" + jobBean.getSiteID()
                                    + "] executed in : " + (endDate.getTime() - startDate.getTime()) + "ms.");
                        }
                    } else if (logger.isDebugEnabled())
                        logger.debug("La notification biquotidienne est d" + Formatter._Character.EACUTE + "sactiv"
                                + Formatter._Character.EACUTE + "e sur le site : " + jahiaSite.getSiteKey());
                }
                return null;
            }
        });

    }

    public void doSendEveryFourDaysMailNotifications() throws RepositoryException, JahiaException {
        final Iterator<JahiaSite> jahiaSites = ServicesRegistry.getInstance().getJahiaSitesService().getSites();
        JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", Locale.FRENCH, new JCRCallback<Object>() {
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                while (jahiaSites.hasNext()) {
                    JahiaSite jahiaSite = jahiaSites.next();
                    NotificationJobBean jobBean = new NotificationJobBean();
                    try {
                        initMailTemplateContributedElements(jahiaSite, jobBean);
                    } catch (JahiaException e) {
                        logger.error("doSendDailyMailNotifications - Error when trying to init job bean");
                    }
                    if (jobBean.isNotificationActivated()) {
                        Date startDate = new Date();
                        if (logger.isInfoEnabled())
                            logger.info("Starting four days notification on site \"" + jahiaSite.getTitle() + "\" [id="
                                    + jobBean.getSiteID() + "]");

                        jobBean.setEveryFourDays(true);
                        prepareNewQuestionsMap(session, jobBean);
                        prepareUsersToNotify(session, jobBean);
                        sendMails(session, jobBean);
                        if (logger.isInfoEnabled()) {
                            Date endDate = new Date();
                            logger.info("Four days notification on site \"" + jahiaSite.getTitle() + "\" [id=" + jobBean.getSiteID()
                                    + "] executed in : " + (endDate.getTime() - startDate.getTime()) + "ms.");
                        }
                    } else if (logger.isDebugEnabled())
                        logger.debug("La notification quadri-quotidienne est d" + Formatter._Character.EACUTE + "sactiv"
                                + Formatter._Character.EACUTE + "e sur le site : " + jahiaSite.getSiteKey());
                }
                return null;
            }
        });
    }

    /**
     * Envoyer les emails
     *
     * @throws ItemNotFoundException
     * @throws RepositoryException
     */
    void sendMails(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        // Envoyer les mails
        JahiaUserManagerService jums = ServicesRegistry.getInstance().getJahiaUserManagerService();
        for (JCRNodeWrapper user : jobBean.getUsers().values()) {
            try {
                MailNotificationBean notification = new MailNotificationBean();
                String selectedThematics = user.getPropertyAsString(PROPERTIES_USER_SELECTED_THEMATICS);
                String selectedRubrics = user.getPropertyAsString(PROPERTIES_USER_SELECTED_RUBRICS);
                String mailTo = user.getPropertyAsString(PROPERTIES_USER_MAIL);
                String userIdentifier = "";
                String userPseudo = "";
                String templatePath = notification.getMailMemberTemplate();
                boolean isModerator = jums.lookupUser(user.getName()).isMemberOfGroup(jobBean.getSiteID(), USER_GROUP_MODERATOR);
                if (isModerator) {
                    templatePath = notification.getMailModeratorTemplate();
                    selectedThematics = jobBean.getAllThematics();
                } else if (UserHelper.isProfessional(user))
                    templatePath = notification.getMailProfessionalTemplate();

                try {
                    userIdentifier = user.getIdentifier();
                    userPseudo = user.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME);
                } catch (Exception e1) {
                    logger.error("User property " + PROPERTIES_USER_PSEUDODENAME + " is not existing");
                }

                Map<String, Object> bindings = new HashMap<String, Object>();

                // Bloc nouveaux articles
                String themathicsAndRubricsString = (selectedThematics == null ? StringUtils.EMPTY : selectedThematics)
                        + (selectedRubrics == null ? StringUtils.EMPTY : "," + selectedRubrics);
                String[] themathicsAndRubrics = themathicsAndRubricsString.split(",");
                for (String thematicOrRubric : themathicsAndRubrics) {
                    List<NewArticleBean> articles = new ArrayList<NewArticleBean>();
                    String thematicTitle = thematicOrRubric;
                    if (jobBean.getNewArticlesList() != null) {
                        for (NewArticleBean article : jobBean.getNewArticlesList()) {
                            if (article.getRelatedThematics().contains(thematicOrRubric)) {
                                boolean articlePresent = false;
                                //s'assurer que l'article n'est déjà présent
                                for (String key : notification.getNewArticles().keySet()) {
                                    if (notification.getNewArticles().get(key).contains(article)) {
                                        articlePresent = true;
                                        break;
                                    }
                                }

                                if (!articlePresent) {
                                    articles.add(article);
                                    thematicTitle = article.getThematic();
                                }
                            }
                        }

                        if (!articles.isEmpty()) {
                            if (null != articles && !articles.isEmpty()) {
                                notification.getNewArticles().put(thematicTitle, articles);
                            }
                        }
                    }
                }

                // Bloc Mes questions repondues
                if (UserHelper.isMember(user) && jobBean.getAnsweredUsersMap().containsKey(userIdentifier)) {
                    for (JCRNodeWrapper node : jobBean.getAnsweredUsersMap().get(userIdentifier)) {
                        Map<String, String> values = new HashMap<String, String>();
                        values.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
                        values.put("url", JahiaTools.getUrl(jobBean.getSiteAddress(), node, "jnt:page")
                                + "?orderby=lastmod");
                        notification.getMyQuestionsList().add(values);
                    }
                }

                if (selectedThematics != null) {
                    StringTokenizer st = new StringTokenizer(selectedThematics, ",");
                    while (st != null && st.hasMoreTokens()) {
                        String thematic = st.nextToken();
                        if (StringUtils.isEmpty(thematic)) {
                            continue;
                        }
                        String thematicName = thematic;
                        try {
                            thematicName = PageHelper.getPageTitle(session.getNodeByUUID(thematic));
                            thematicName = StringEscapeUtils.escapeHtml(thematicName);
                        } catch (Exception ignored) {
                        }

                        if (isModerator && !jobBean.getThematicsHavingPro().containsKey(thematic)) {
                            thematicName = thematicName + " (Sans professionnel)";
                        }

                        // Bloc Nouvelles questions :
                        if (jobBean.getQuestionsMap().containsKey(thematic)) {
                            List<JCRNodeWrapper> questions = jobBean.getQuestionsMap().get(thematic);
                            List<Map<String, String>> newQuestionsList = new ArrayList<Map<String, String>>();
                            notification.getNewQuestionsMap().put(thematicName, newQuestionsList);
                            for (JCRNodeWrapper node : questions) {
                                Map<String, String> values = new HashMap<String, String>();
                                newQuestionsList.add(values);
                                values.put("title", StringEscapeUtils.escapeHtml(node.getPropertyAsString("title")));
                                values.put("url", JahiaTools.getUrl(jobBean.getSiteAddress(), node, "jnt:page"));
                                try {
                                    JCRNodeWrapper questionUser = (JCRNodeWrapper) node.getProperty("user").getNode();
                                    values.put("pseudo", questionUser.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME));
                                    values.put("status", UserHelper.getUserStatus(questionUser));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                        // Bloc Nouvelles réponses :
                        if ((UserHelper.isProfessional(user) || isModerator) && jobBean.getRepliesMap().containsKey(thematic)) {
                            Map<String, List<JCRNodeWrapper>> repliesAndQuestions = jobBean.getRepliesMap().get(thematic);
                            Map<String, List<Map<String, String>>> newRepliesMap = new HashMap<String, List<Map<String, String>>>();
                            notification.getNewRepliesMap().put(thematicName, newRepliesMap);

                            for (String questionIdentifier : repliesAndQuestions.keySet()) {
                                newRepliesMap.put(questionIdentifier, new ArrayList<Map<String, String>>());
                                if (newRepliesMap.get(questionIdentifier).size() == 0) {
                                    JCRNodeWrapper question = session.getNodeByIdentifier(questionIdentifier);
                                    Map<String, String> values = new HashMap<String, String>();
                                    newRepliesMap.get(questionIdentifier).add(values);
                                    values.put("title", StringEscapeUtils.escapeHtml(question.getPropertyAsString("title")));
                                    try {
                                        JCRNodeWrapper questionUser = (JCRNodeWrapper) question.getProperty("user").getNode();
                                        values.put("pseudo", questionUser.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME));
                                        values.put("status", UserHelper.getUserStatus(questionUser));
                                    } catch (Exception ignored) {
                                    }
                                }
                                int cpt = 0;
                                for (JCRNodeWrapper reply : repliesAndQuestions.get(questionIdentifier)) {
                                    Map<String, String> values = new HashMap<String, String>();
                                    newRepliesMap.get(questionIdentifier).add(values);
                                    values.put("title", StringUtil.cutString(
                                            StringEscapeUtils.escapeHtml(Functions.removeHtmlTags(reply.getPropertyAsString("body"))), 140));
                                    if (++cpt == repliesAndQuestions.get(questionIdentifier).size()) {
                                        values.put("url", JahiaTools.getUrl(jobBean.getSiteAddress(), reply, "jnt:page"));
                                    }
                                    try {
                                        JCRNodeWrapper replyUser = (JCRNodeWrapper) reply.getProperty("user").getNode();
                                        if (UserHelper.isMember(replyUser)) {
                                            // already escaped in UserHelper.getUserStatus()
                                            String status = UserHelper.getUserStatus(replyUser);

                                            String pseudo = replyUser.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME);
                                            pseudo = StringEscapeUtils.escapeHtml(pseudo);

                                            values.put("pseudo", pseudo);
                                            values.put("status", status);
                                        } else {
                                            String pseudo = replyUser.getPropertyAsString(PROPERTIES_USER_FIRSTNAME) + " "
                                                    + replyUser.getPropertyAsString(PROPERTIES_USER_LASTAME);
                                            pseudo = StringEscapeUtils.escapeHtml(pseudo);

                                            String status = replyUser.getPropertyAsString("j:function");
                                            status = StringEscapeUtils.escapeHtml(status);

                                            if (StringUtils.isEmpty(status)) {
                                                if (UserHelper.isProfessional(replyUser))
                                                    status = "Professionnel";
                                                else
                                                    status = "Mod&eacute;rateur";
                                            }

                                            values.put("pseudo", pseudo);
                                            values.put("status", status);
                                        }
                                        values.put("date",
                                                dateFormatter.format(reply.getProperty(PROPERTIES_JCR_CREATED).getDate().getTime()));
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    }
                }

                bindings.put("notificationBean", notification);
                bindings.put("user", userPseudo);

                bindings.put("siteAddress", jobBean.getSiteAddress());
                bindings.put("gaSuffixExpert", jobBean.getGaSuffixExperts());
                bindings.put("gaSuffixMembre", jobBean.getGaSuffixMembres());
                bindings.put("logo", jobBean.getSiteAddress() + jobBean.getMailTemplateLogo());
                bindings.put("line", jobBean.getSiteAddress() + jobBean.getMailTemplateLine());
                bindings.put("border", jobBean.getSiteAddress() + jobBean.getMailTemplateBorder());

                if (jobBean.isDaily()) {
                    bindings.put("mailSubject", "Pr\u00E9paronsmaretraite.fr - votre r\u00E9cap' du jour");
                    bindings.put("questionsBlocTitle", "Nouvelles questions pos&eacute;es aujourd'hui : ");
                    bindings.put("articlesBlocTitle", "Nouveaux articles publi&eacute;s aujourd'hui : ");
                }
                if (jobBean.isEveryTwoDays()) {
                    bindings.put("mailSubject", "Pr\u00E9paronsmaretraite.fr - Question(s) rest\u00E9e(s) sans r\u00E9ponse depuis 2 jours");
                    bindings.put("questionsBlocTitle", "Ces questions n'ont pas obtenu de r&eacute;ponse depuis 2 jours : ");
                }
                if (jobBean.isEveryFourDays()) {
                    bindings.put("mailSubject", "Pr\u00E9paronsmaretraite.fr - Question(s) rest\u00E9e(s) sans r\u00E9ponse depuis 4 jours");
                    bindings.put("questionsBlocTitle", "Ces questions n'ont pas obtenu de r&eacute;ponse depuis 4 jours : ");
                }

                try {
                    if (notification.getMyQuestionsList().size() > 0 || notification.getNewQuestionsMap().size() > 0
                            || notification.getNewRepliesMap().size() > 0
                            || notification.getNewArticles().size() > 0) {
                        ServicesRegistry
                                .getInstance()
                                .getMailService()
                                .sendMessageWithTemplate(templatePath, bindings, mailTo, jobBean.getEmailNoReply(), null, null,
                                        session.getLocale(), CR_TEMPLATE_PACKAGE_NAME);
                    }
                } catch (RepositoryException e) {
                    logger.error("Error sending Notification's Mail to : " + mailTo + " : " + e);
                } catch (ScriptException e) {
                    logger.error("Error sending Notification's Mail to : " + mailTo + " : " + e);
                }
            } catch (Exception e) {
                logger.error("Erreur de g" + Formatter._Character.EACUTE + "n" + Formatter._Character.EACUTE
                        + "ration de mail de notification quotidienne ", e);
            }
        }
    }

    void prepareUsersToNotify(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        jobBean.setUsers(new HashMap<String, JCRNodeWrapper>());
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Set<String> thematics = new HashSet<String>();

        thematics.addAll(jobBean.getQuestionsMap().keySet());
        thematics.addAll(jobBean.getRepliesMap().keySet());

        if (jobBean.getNewArticlesList() != null) {
            for (NewArticleBean article : jobBean.getNewArticlesList()) {
                thematics.addAll(article.getRelatedThematics());
            }
        }
        jobBean.setAllThematics(StringUtils.join(thematics, ","));

        if (jobBean.isDaily() || jobBean.isEveryTwoDays()) {
            Iterator<String> it = thematics.iterator();
            StringBuilder query = new StringBuilder("SELECT * FROM [jnt:user] as user ");
            if (thematics.size() > 0) {
                query.append(" WHERE ");

                if (jobBean.isEveryTwoDays()) {
                    query.append(" isProfessional = 'true' AND ");
                }

                // TODO - test adding mail filled and account locked clause
                // information
                query.append(" user.[" + PROPERTIES_USER_MAIL + "] <> '' AND ");
                query.append(" user.[" + PROPERTIES_USER_ACCOUNT_LOCKED + "] <> 'true' AND ");
                String id = it.next();
                query.append(" user.acceptNotification = 'true' AND (user.selectedThematics like '%").append(id).append("%' OR user.selectedRubrics like '%").append(id).append("%'");
                while (it.hasNext()) {
                    id = it.next();
                    query.append(" OR user.selectedThematics like '%").append(id).append("%'");
                    query.append(" OR user.selectedRubrics like '%").append(id).append("%'");
                }
                query.append(")");
                Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
                QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
                NodeIterator iterator = queryResult.getNodes();
                while (iterator.hasNext()) {
                    JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
                    jobBean.getUsers().put(node.getIdentifier(), node);
                }
            }
        }

        // utilisateurs repondus :
        if (jobBean.isDaily()) {
            for (String identifier : jobBean.getAnsweredUsersMap().keySet()) {
                JCRNodeWrapper node = session.getNodeByUUID(identifier);
                if (node != null && !jobBean.getUsers().containsKey(node.getIdentifier())) {
                    jobBean.getUsers().put(node.getIdentifier(), node);
                }
            }
        }

        // moderateurs :
        JahiaGroupManagerRoutingService groupService = JahiaGroupManagerRoutingService.getInstance();
        if (groupService.groupExists(jobBean.getSiteID(), USER_GROUP_MODERATOR)) {
            JahiaGroup group = groupService.lookupGroup(jobBean.getSiteID(), USER_GROUP_MODERATOR);
            if (group != null) {
                Set<Principal> moderators = group.getRecursiveUserMembers();
                for (Principal modo : moderators) {
                    JCRUser jcrModo = (JCRUser) modo;
                    if (jcrModo != null && !jobBean.getUsers().containsKey(jcrModo.getIdentifier())) {
                        jobBean.getUsers().put(jcrModo.getIdentifier(), jcrModo.getNode(session));
                    }
                }
            }
        }

        // Thematics - has pro assigned or not - :
        Iterator<String> it = thematics.iterator();
        if (thematics.size() > 0) {
            while (it.hasNext()) {
                try {
                    String thematic = it.next();
                    Query q = queryManager.createQuery("SELECT * FROM [jnt:user] as user WHERE isProfessional = 'true' AND " + " user.selectedThematics like '%" + thematic + "%'", Query.JCR_SQL2);
                    QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
                    if (queryResult.getNodes().getSize() > 0) {
                        jobBean.getThematicsHavingPro().put(thematic, Boolean.TRUE);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Préparer la liste des nouveaux articles
     * <p>
     * Liste contiens | Liste des UUID des Thématiques | Les Articles liés au ces thématiques
     */
    void prepareNewArticlesMap(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        if (logger.isInfoEnabled())
            logger.info("Preparing new articles map for site notifications [id=" + jobBean.getSiteID() + "] ...");

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        if (jobBean.isDaily())
            startDate.add(Calendar.DATE, -1);
        if (jobBean.isEveryTwoDays()) {
            startDate.add(Calendar.DATE, -4);
            endDate.add(Calendar.DATE, -2);
        }
        if (jobBean.isEveryFourDays()) {
            startDate.add(Calendar.DATE, -30);
            endDate.add(Calendar.DATE, -4);
        }

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        StringBuilder query = new StringBuilder("SELECT * FROM [" + PROPERTIES_ARTICLE_PRIMARY_TYPE + "] as node ");
        query.append(" WHERE node.[jcr:created] > CAST(").append(startDate.getTime().getTime()).append(" AS DATE) ");

        if (jobBean.isEveryTwoDays() || jobBean.isEveryFourDays())
            query.append(" AND node.[jcr:created] < CAST(").append(endDate.getTime().getTime()).append(" AS DATE) AND node.").append(PROPERTIES_FORUM_NB_OF_PROF_REPLIES).append(" = 0");

        query.append(" ORDER BY [jcr:created] ASC");
        Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
        QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
        NodeIterator iterator = queryResult.getNodes();

        List<NewArticleBean> list = MailNotificationServiceHelper.populateNewArticles(jobBean, iterator);
        jobBean.setNewArticlesList(list);
    }

    /**
     * Preparer la liste des questions par thematiques. Ces questions peuvent
     * etre soit des questions non repondues (pour les moderateurs et
     * prodesssionnels _tous les 2 ou 4jours_), soit les questions du jour.
     * <p>
     * Les questions sont rangees par thematiques dans une map de la forme :
     * <ul>
     * <li>|- uuid thematique 1 > |--- question 1 |--- question 2 |--- (...)</li>
     * <li>|- uuid thematique 2 > |--- question 4 |--- question 5 |--- (...)</li>
     * </ul>
     *
     * @throws RepositoryException
     */
    void prepareNewQuestionsMap(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        jobBean.setQuestionsMap(new HashMap<String, List<JCRNodeWrapper>>());

        if (logger.isInfoEnabled())
            logger.info("Preparing new questions map for site notifications [id=" + jobBean.getSiteID() + "] ...");

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        if (jobBean.isDaily())
            startDate.add(Calendar.DATE, -1);
        if (jobBean.isEveryTwoDays()) {
            startDate.add(Calendar.DATE, -4);
            endDate.add(Calendar.DATE, -2);
        }
        if (jobBean.isEveryFourDays()) {
            startDate.add(Calendar.DATE, -30);
            endDate.add(Calendar.DATE, -4);
        }

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        StringBuilder query = new StringBuilder("SELECT * FROM [" + PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE + "] as node ");
        query.append(" WHERE node.[jcr:created] > CAST(").append(startDate.getTime().getTime()).append(" AS DATE) ");

        if (jobBean.isEveryTwoDays() || jobBean.isEveryFourDays())
            query.append(" AND node.[jcr:created] < CAST(").append(endDate.getTime().getTime()).append(" AS DATE) AND node.").append(PROPERTIES_FORUM_NB_OF_PROF_REPLIES).append(" = 0");

        query.append(" ORDER BY [jcr:created] ASC");
        Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
        QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
        NodeIterator iterator = queryResult.getNodes();

        while (iterator.hasNext()) {
            try {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
                JCRNodeWrapper thematicNode = PageHelper.getThematicParent(node);

                if (thematicNode != null) {
                    populateQuestionsMap(jobBean.getQuestionsMap(), thematicNode.getIdentifier(), node);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage() + " " + e.getCause());
            }
        }
    }

    /**
     * Preparer la liste des nouvelles reponses du jour.
     * <p>
     * Les reponses sont rangees dans une map de la forme :
     * <ul>
     * <li>|- uuid thematique 1 > |--- reponse 1 |--- reponse 2 |--- (...)</li>
     * <li>|- uuid thematique 2 > |--- reponse 4 |--- reponse 5 |--- (...)</li>
     * </ul>
     *
     * @throws RepositoryException
     */
    void prepareNewRepliesMap(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        jobBean.setRepliesMap(new HashMap<String, Map<String, List<JCRNodeWrapper>>>());

        if (logger.isInfoEnabled())
            logger.info("Preparing new replies map for site notifications [id=" + jobBean.getSiteID() + "] ...");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query q = queryManager.createQuery("SELECT * FROM [" + PROPERTIES_FORUM_REPLY_PRIMARY_TYPE + "] as node " + " WHERE node.[jcr:created] > CAST(" + c.getTime().getTime() + " AS DATE) " + " ORDER BY [jcr:created] ASC", Query.JCR_SQL2);
        QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
        NodeIterator iterator = queryResult.getNodes();

        while (iterator.hasNext()) {
            try {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
                JCRNodeWrapper thematicNode = PageHelper.getThematicParent(node);

                if (thematicNode != null) {
                    populateRepliesMap(jobBean.getRepliesMap(), thematicNode.getIdentifier(), node);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage() + " " + e.getCause());
            }
        }
    }

    /**
     * Preparer la liste des questions repondues par user (mail aux membres).
     * Map de la forme :
     * <p>
     * <ul>
     * <li>|- uuid user 1 > |--- question 1 |--- question 2 |--- (...)</li>
     * <li>|- uuid user 2 > |--- question 4 |--- question 5 |--- (...)</li>
     * </ul>
     *
     * @throws RepositoryException
     */
    void prepareAnsweredUsersMap(JCRSessionWrapper session, NotificationJobBean jobBean) throws RepositoryException {
        jobBean.setAnsweredUsersMap(new HashMap<String, List<JCRNodeWrapper>>());

        if (logger.isInfoEnabled())
            logger.info("Preparing answered questions map for site notifications [id=" + jobBean.getSiteID() + "] ...");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query q = queryManager.createQuery("SELECT * FROM [jnt:ciQuestion] as node " + " WHERE node." + PROPERTIES_FORUM_MODIFIED_DATE + " > CAST(" + c.getTime().getTime() + " AS DATE) " + " AND node." + PROPERTIES_FORUM_NB_OF_REPLIES + " > 0 ORDER BY [" + PROPERTIES_FORUM_MODIFIED_DATE + "] ASC", Query.JCR_SQL2);
        QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
        NodeIterator iterator = queryResult.getNodes();

        while (iterator.hasNext()) {
            try {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
                JCRPropertyWrapper userNode = node.getProperty("user");

                if (userNode != null) {
                    populateQuestionsMap(jobBean.getAnsweredUsersMap(), userNode.getNode().getIdentifier(), node);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    /**
     * Rajoute un question node a la liste de la map.
     *
     * @param map                (map) Map<String, List<JCRNodeWrapper>>
     * @param thematicOrUserUuid (cle du noeud jcr associe)
     * @param questionNode       (element de la liste)
     */
    void populateQuestionsMap(Map<String, List<JCRNodeWrapper>> map, String thematicOrUserUuid, JCRNodeWrapper questionNode) {
        if (map.containsKey(thematicOrUserUuid)) {
            List<JCRNodeWrapper> list = map.get(thematicOrUserUuid);
            list.add(questionNode);
        } else {
            List<JCRNodeWrapper> list = new ArrayList<JCRNodeWrapper>();
            list.add(questionNode);
            map.put(thematicOrUserUuid, list);
        }
    }

//	private void populateArticleMap(Map<ArrayList<String>, List<JCRNodeWrapper>> newArticlesMap,
//			List<JCRNodeWrapper> thematicNodesList, JCRNodeWrapper node) {
//		
//	}

    /**
     * Rajoute un reply node a la liste de la map.
     *
     * @param replyMap     (map) Map<String, List<JCRNodeWrapper>> the map of new replies
     *                     of the day
     * @param thematicUuid (cle) string identifier of thematic reply's parent node
     * @param node         (element de la liste) the node of the reply to add
     * @throws RepositoryException
     */
    void populateRepliesMap(Map<String, Map<String, List<JCRNodeWrapper>>> replyMap, String thematicUuid, JCRNodeWrapper node)
            throws RepositoryException {
        String parentIdentifier = node.getParent().getIdentifier();
        if (replyMap.containsKey(thematicUuid)) {
            Map<String, List<JCRNodeWrapper>> list = replyMap.get(thematicUuid);
            if (list.containsKey(parentIdentifier)) {
                list.get(parentIdentifier).add(node);
            } else {
                List<JCRNodeWrapper> tmp = new ArrayList<JCRNodeWrapper>();
                tmp.add(node);
                list.put(parentIdentifier, tmp);
            }

        } else {
            Map<String, List<JCRNodeWrapper>> list = new HashMap<String, List<JCRNodeWrapper>>();
            replyMap.put(thematicUuid, list);

            if (list.containsKey(parentIdentifier)) {
                list.get(parentIdentifier).add(node);
            } else {
                List<JCRNodeWrapper> tmp = new ArrayList<JCRNodeWrapper>();
                tmp.add(node);
                list.put(parentIdentifier, tmp);
            }
        }
    }

    /**
     * Checks notification site's parameters. Also populates NotificationJobBean
     * object.
     *
     * @throws RepositoryException
     * @throws JahiaException
     */
    void initMailTemplateContributedElements(final JahiaSite site, final NotificationJobBean jobBean) throws
            RepositoryException, JahiaException {

        if (logger.isDebugEnabled())
            logger.debug("Initiates job bean for site : " + site.getSiteKey() + "[id=" + site.getID() + "]...");

        JCRTemplate.getInstance().doExecuteWithSystemSession("root", "default", Locale.FRENCH, new JCRCallback<Object>() {
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                jobBean.setSiteID(site.getID());
                JCRSiteNode node = session.getNodeByUUID(site.getUuid()).getResolveSite();
                if (node.hasProperty(NOTIFICATION_ACTIVATED) && node.getProperty(NOTIFICATION_ACTIVATED).getBoolean()) {
                    // very important thing lol
                    jobBean.setNotificationActivated(true);

                    jobBean.setSiteAddress(node.getPropertyAsString(NOTIFICATION_SITE_ADDRESS));
                    jobBean.setGaSuffixExperts(node.getPropertyAsString(NOTIFICATION_GOOGLE_ANALYTICS_URL_SUFFIX_EXPERTS));
                    jobBean.setGaSuffixMembres(node.getPropertyAsString(NOTIFICATION_GOOGLE_ANALYTICS_URL_SUFFIX_MEMBRES));
                    jobBean.setEmailNoReply(node.getPropertyAsString(NOTIFICATION_EMAIL_NO_REPLY));

                    if (node.hasProperty(NOTIFICATION_MAIL_TEMPLATE_LOGO)) {
                        jobBean.setMailTemplateLogo(((JCRNodeWrapper) node.getProperty(NOTIFICATION_MAIL_TEMPLATE_LOGO).getNode()).getUrl());
                        jobBean.setMailTemplateLogo(StringUtils.replace(jobBean.getMailTemplateLogo(), "default", "live"));
                    }
                    if (node.hasProperty(NOTIFICATION_MAIL_TEMPLATE_LINE)) {
                        jobBean.setMailTemplateLine(((JCRNodeWrapper) node.getProperty(NOTIFICATION_MAIL_TEMPLATE_LINE).getNode()).getUrl());
                        jobBean.setMailTemplateLine(StringUtils.replace(jobBean.getMailTemplateLine(), "default", "live"));
                    }
                    if (node.hasProperty(NOTIFICATION_MAIL_TEMPLATE_BORDER)) {
                        jobBean.setMailTemplateBorder(((JCRNodeWrapper) node.getProperty(NOTIFICATION_MAIL_TEMPLATE_BORDER).getNode())
                                .getUrl());
                        jobBean.setMailTemplateBorder(StringUtils.replace(jobBean.getMailTemplateBorder(), "default", "live"));
                    }
                } else
                    // useless but why not
                    jobBean.setNotificationActivated(false);
                return null;
            }
        });
    }

}
