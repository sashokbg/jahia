package org.jahia.modules.ci.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CharPool;
import org.commons.util.CiConstants;
import org.jahia.api.Constants;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.seo.VanityUrl;
import org.jahia.services.seo.jcr.VanityUrlManager;
import org.jahia.taglibs.jcr.node.JCRTagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageHelper implements CiConstants {

	private static Logger logger = LoggerFactory.getLogger(PageHelper.class);

	public static JCRNodeWrapper getParentOfType(JCRNodeWrapper node, String type) {
		JCRNodeWrapper matchingParent = null;
		if (node == null) {
			return null;
		}
		try {
			JCRNodeWrapper parent = node.getParent();
			while (parent != null) {
				if (parent.isNodeType(type)) {
					matchingParent = parent;
					break;
				}
				parent = parent.getParent();
			}
		} catch (ItemNotFoundException e) {
			// we reached the hierarchy top
		} catch (RepositoryException e) {
			logger.error("Error while retrieving nodes parent node. Cause: " + e.getMessage(), e);
		}
		return matchingParent;
	}

	public static JCRNodeWrapper getRubricParent(JCRNodeWrapper node) {
		JCRNodeWrapper matchingParent = null;
		if (node == null) {
			return null;
		}
		try {
			JCRNodeWrapper parent = node;
			while (parent != null) {
				if (parent.hasProperty(PROPERTIES_PAGE_ISRUBRIC)
						&& StringUtils.equalsIgnoreCase(parent.getPropertyAsString(PROPERTIES_PAGE_ISRUBRIC), "true")) {
					matchingParent = parent;
					break;
				}
				parent = getParentOfType(parent, "jnt:page");
			}
		} catch (ItemNotFoundException e) {
			// we reached the hierarchy top
		} catch (RepositoryException e) {
			logger.error("Error while retrieving nodes parent node. Cause: " + e.getMessage(), e);
		}
		return matchingParent;
	}

	public static JCRNodeWrapper getThematicParent(JCRNodeWrapper node) {
		JCRNodeWrapper matchingParent = null;
		if (node == null) {
			return null;
		}
		try {
			JCRNodeWrapper parent = node;
			while (parent != null) {
				if (parent.hasProperty(PROPERTIES_PAGE_ISTHEMATIC)
						&& StringUtils.equalsIgnoreCase(parent.getPropertyAsString(PROPERTIES_PAGE_ISTHEMATIC), "true")) {
					matchingParent = parent;
					break;
				}
				parent = getParentOfType(parent, "jnt:page");
			}
		} catch (ItemNotFoundException e) {
			// we reached the hierarchy top
		} catch (RepositoryException e) {
			logger.error("Error while retrieving nodes parent node. Cause: " + e.getMessage(), e);
		}
		return matchingParent;
	}

	/**
	 * Give the available page title under another page node.
	 * 
	 * @param session
	 *            the current jcr session
	 * @param pageTitle
	 *            the title to set
	 * @param parentPageNodePath
	 *            the page node under which we want to change or create the page
	 *            node title
	 * @param theNode
	 *            the page node for which we want to change the title
	 * @return the available title
	 * @throws RepositoryException
	 * 
	 */
	public synchronized static String getProperTitle(JCRSessionWrapper session, String pageTitle, String parentPageNodePath,
			JCRNodeWrapper theNode) throws RepositoryException {

		final VanityUrlManager vanityUrlManager = (VanityUrlManager) SpringContextSingleton.getBean(VanityUrlManager.class.getName());
		final List<JCRNodeWrapper> subPages = JCRTagUtils.getChildrenOfType(session.getNode(parentPageNodePath), Constants.JAHIANT_PAGE);
		final String siteKey = JCRContentUtils.getSiteKey(parentPageNodePath);

		int suffixCounter = 0;
		String titleToTest = pageTitle;
		String absPathToTest = parentPageNodePath + CharPool.SLASH + titleToTest;
		String urlToTest = StringUtils.substringAfter(absPathToTest + ".html", siteKey);
		// verifying if a vanity url possibly exists with same name
		List<VanityUrl> foundSiteVanityUrls = vanityUrlManager.findExistingVanityUrls(urlToTest, siteKey, session);
		boolean notEmptyList = false;

		while (notEmptyList && suffixCounter < 1024) {
			suffixCounter++;
			titleToTest = pageTitle + "-" + suffixCounter;
			absPathToTest = parentPageNodePath + CharPool.SLASH + titleToTest;
			urlToTest = StringUtils.substringAfter(absPathToTest + ".html", siteKey);
			foundSiteVanityUrls = vanityUrlManager.findExistingVanityUrls(urlToTest, siteKey, session);
			notEmptyList = foundSiteVanityUrls.size() > 0;
		}

		for (JCRNodeWrapper page : subPages) {
			if (theNode == null || !theNode.isSame(page)) {
				notEmptyList = false;
				while ((notEmptyList || session.nodeExists(absPathToTest)
						&& (theNode == null || theNode != null && !session.getNode(absPathToTest).isSame(theNode))) && suffixCounter < 1024) {
					suffixCounter++;
					titleToTest = pageTitle + "-" + suffixCounter;
					absPathToTest = parentPageNodePath + CharPool.SLASH + titleToTest;
					urlToTest = StringUtils.substringAfter(absPathToTest + ".html", siteKey);
					foundSiteVanityUrls = vanityUrlManager.findExistingVanityUrls(urlToTest, siteKey, session);
					notEmptyList = foundSiteVanityUrls.size() > 0;
				}
			}
		}

		return titleToTest;
	}

	/**
	 * Recupere le titre d'une page
	 * 
	 * @param pageNode
	 * @return
	 * @throws PathNotFoundException
	 */
	public static String getPageTitle(JCRNodeWrapper pageNode) throws PathNotFoundException {
		String title = "";
		try {
			String lang = pageNode.getLanguage();
			if (lang == null)
				lang = Locale.FRANCE.getLanguage();
			title = pageNode.getNode("j:translation_" + lang).getPropertyAsString("jcr:title");
		} catch (Exception e) {
			logger.error(e.getClass().getName() + "- getPageTitle - " + e.getMessage());
		}
		return title;
	}

	/**
	 * Recupere le short titre d'une page
	 * 
	 * @param pageNode
	 * @return
	 * @throws PathNotFoundException
	 */
	public static String getPageShortTitle(JCRNodeWrapper pageNode) throws PathNotFoundException {
		return pageNode.getPropertyAsString(PROPERTIES_PAGE_SHORT_TITLE);
	}

	/**
	 * Recupere le short titre d'une page
	 * 
	 * @param pageNode
	 * @return
	 * @throws PathNotFoundException
	 */
	public static String getPageLogo(JCRNodeWrapper pageNode) throws PathNotFoundException {
		try {
			return ((JCRNodeWrapper) pageNode.getProperty(PROPERTIES_PAGE_LOGO).getNode()).getUrl();
		} catch (Exception e) {
			logger.error(e.getClass().getName() + "- getPageLogo - on node " + pageNode + "\n\r" + e.getMessage());
			return "";
		}
	}

	public static List<JCRNodeWrapper> getParentPagesList(JCRNodeWrapper node) {
		List<JCRNodeWrapper> parents = new ArrayList<JCRNodeWrapper>();
		do {
			node = getParentOfType(node, Constants.JAHIANT_PAGE);
			if (node != null) {
				parents.add(node);
			}
		} while (node != null);
		Collections.reverse(parents);
		return parents;
	}
	
	/**
	 * Recupere la liste des pages parents (utilisation dans le breadcrumb)
	 * 
	 * @param node
	 * @param thematicsOnly TODO
	 * @return
	 */
	public static List<JCRNodeWrapper> getParentPagesList(JCRNodeWrapper node, boolean thematicsOnly) {
		List<JCRNodeWrapper> parents = new ArrayList<JCRNodeWrapper>();
		do {
			node = getParentOfType(node, Constants.JAHIANT_PAGE);
			if (node != null) {
				if(thematicsOnly){
					try {
						if (node.hasProperty(PROPERTIES_PAGE_ISTHEMATIC)){
							parents.add(node);
						}
					} catch (RepositoryException e) {
						logger.error("Error while retrieving nodes parent node. Cause: " + e.getMessage(), e);
					}
				}
				else{
					parents.add(node);
				}
			}
		} while (node != null);
		Collections.reverse(parents);
		return parents;
	}

	/**
	 * Recupere la liste des pages parents qui sont thématiques(utilisation dans le breadcrumb)
	 * 
	 * @param node
	 * @return
	 */
	public static List<JCRNodeWrapper> getListThematicParents(JCRNodeWrapper node) {
		return getParentPagesList(node,true);
	}
}
