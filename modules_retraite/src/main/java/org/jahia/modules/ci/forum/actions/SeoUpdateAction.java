package org.jahia.modules.ci.forum.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.seo.VanityUrl;
import org.jahia.services.seo.jcr.VanityUrlManager;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.json.JSONObject;

public class SeoUpdateAction extends Action implements CiConstants {

	JCRTemplate jcrTemplate;

	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, final URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		final VanityUrlManager vanityUrlManager = (VanityUrlManager) SpringContextSingleton.getBean(VanityUrlManager.class.getName());

		boolean isUpdated = (Boolean) jcrTemplate.doExecuteWithSystemSession(user.getName(), session.getWorkspace().getName(),
				session.getLocale(), new JCRCallback<Boolean>() {
					public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
						JCRNodeWrapper currentNode = resource.getNode();
						currentNode = session.getNodeByIdentifier(currentNode.getIdentifier());
						String questionTitle = getParameter(parameters, "title");
						String questionNormalizedTitle = Formatter.normalizeText(questionTitle, PROPERTIES_FORUM_TITLE_CHARS_TO_REPLACE);

						if (questionNormalizedTitle.length() > 33)
							questionNormalizedTitle = questionNormalizedTitle.substring(0, 32);

						JCRNodeWrapper parentPageNode = PageHelper.getParentOfType(currentNode, Constants.JAHIANT_PAGE);
						JCRNodeWrapper forumPageNode = PageHelper.getParentOfType(parentPageNode, Constants.JAHIANT_PAGE);
						String vanityTitle = forumPageNode.getPath().replace(renderContext.getSite().getPath(), "");

						questionNormalizedTitle = PageHelper.getProperTitle(session, questionNormalizedTitle, forumPageNode.getPath(),
								parentPageNode);
						if (currentNode.isNodeType(PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE)) {
							final String languageCode = session.getLocale().getLanguage();
							VanityUrl vanityUrl = new VanityUrl(vanityTitle + "/" + questionNormalizedTitle + ".html", urlResolver
									.getSiteKey(), languageCode, true, true);

							/*
							 * This code will be useless when Jahia will resolve
							 * the org.jahia.services.seo.jcr.VanityUrlManager
							 * issue
							 * 
							 * @author foo
							 * 
							 * @see https://support.jahia.com/browse/AGDR-82
							 */
							List<VanityUrl> vanityUrls = null;

							if (parentPageNode.isNodeType(VanityUrlManager.JAHIAMIX_VANITYURLMAPPED))
								vanityUrls = vanityUrlManager.getVanityUrls(parentPageNode, languageCode, session);
							if (vanityUrls == null)
								vanityUrls = new ArrayList<VanityUrl>();

							boolean found = false;

							for (VanityUrl vu : vanityUrls) {
								if (vu.getUrl().equals(vanityUrl.getUrl()) && vu.getSite().equals(vanityUrl.getSite())) {
									vu.setDefaultMapping(true);
									found = true;
								} else
									vu.setDefaultMapping(false);
							}

							if (!found) {
								vanityUrls.add(vanityUrl);
							} else if (vanityUrls.size() > 0) {
								vanityUrlManager.removeVanityUrlMappings(parentPageNode, languageCode, session);
							}

							vanityUrlManager.saveVanityUrlMappings(parentPageNode, vanityUrls,
									new HashSet<String>(Arrays.asList(new String[] { languageCode })), session);
							/*
							 * use only
							 * vanityUrlManager.saveVanityUrlMapping(contentNode
							 * , vanityUrl, session) when updated to 6.6.1.4.
							 */
							session.checkout(currentNode);
							session.checkout(parentPageNode);

							currentNode.setProperty(PROPERTIES_FORUM_POST_TITLE, questionTitle);
							parentPageNode.setProperty(Constants.JCR_TITLE, questionTitle);
							session.save();
							parentPageNode.update("live");
							return true;
						}

						return false;
					}
				});

		if (isUpdated)
			return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject("{reload:true}"));
		else
			return ActionResult.INTERNAL_ERROR_JSON;
	}
}
