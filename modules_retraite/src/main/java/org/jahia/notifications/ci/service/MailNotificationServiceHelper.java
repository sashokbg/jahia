package org.jahia.notifications.ci.service;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;

import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.notifications.ci.beans.NewArticleBean;
import org.jahia.notifications.ci.beans.NotificationJobBean;
import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailNotificationServiceHelper {

	private static final Logger logger = LoggerFactory.getLogger(MailNotificationServiceHelper.class);
	
	/**
	 * Trouver tous les thématiques parents
	 * 
	 * @param iterator
	 * @return
	 */
	public static List<NewArticleBean> populateNewArticles(NotificationJobBean jobBean, NodeIterator iterator) {
		List<NewArticleBean> listNewArticles = new ArrayList<NewArticleBean>();
		
		while (iterator.hasNext()) {
			ArrayList<String> thematicNodesList = new ArrayList<String>();
			List<JCRNodeWrapper> newArticlesList = new ArrayList<JCRNodeWrapper>();
			NewArticleBean article = null;
			
			try {
				JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
				
				if(!newArticlesList.contains(node)){
					newArticlesList.add(node);
				}
					
				for(JCRNodeWrapper thematicNode : PageHelper.getListThematicParents(node)){
					if(!thematicNodesList.contains(thematicNode.getIdentifier())){
						thematicNodesList.add(thematicNode.getIdentifier());
					}
				}
				article = new NewArticleBean();
				article.setNode(node);
				article.setRelatedThematics(thematicNodesList);
				article.setTitle(node.getPropertyAsString("title"));
				article.setThematic(PageHelper.getPageTitle(PageHelper.getThematicParent(node)));
				article.setUrl(jobBean.getSiteAddress() + PageHelper.getParentOfType(node,"jnt:page").getUrl());
			} catch (Exception e) {
				logger.warn(e.getMessage() + " " + e.getCause());
			}
			
			if(null != article){
				listNewArticles.add(article);
			}
		}
		return listNewArticles;
	}
}
