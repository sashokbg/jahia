package org.jahia.modules.ci.forum.actions;

import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.commons.util.GetterUtil;
import org.commons.util.Validator;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddQuestionAction extends Action implements CiConstants {

	JCRTemplate jcrTemplate;

	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		JCRUser user = (JCRUser) session.getUser();
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(user.getName(), session.getWorkspace().getName(), session.getLocale(),
				new JCRCallback<Object>() {
					public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
						JCRUser user = (JCRUser) session.getUser();
						JCRNodeWrapper userNode = user.getNode(session);

						String questionTitle = getParameter(parameters, "title");
						String thematicNodePath = getParameter(parameters, "thematicPath");

						if (StringUtils.isNotEmpty(thematicNodePath) && StringUtils.isNotEmpty(questionTitle)) {
							JCRNodeWrapper thematicNode = session.getNode(thematicNodePath);
							NodeIterator iterator = thematicNode.getNodes();
							JCRNodeWrapper forumPageNode = null;
							while (iterator.hasNext()) {
								JCRNodeWrapper childNode = (JCRNodeWrapper) iterator.next();
								if (Constants.JAHIANT_PAGE.equals(childNode.getPrimaryNodeTypeName())
										&& childNode.hasProperty(PROPERTIES_PAGE_MARKER_ISFORUM)
										&& "true".equals(childNode.getPropertyAsString(PROPERTIES_PAGE_MARKER_ISFORUM))) {
									forumPageNode = childNode;
									break;
								}
							}

							if (Validator.isNotNull(forumPageNode)) {
								JCRNodeWrapper dummyPageNode = getDummyQuestionPageNode(renderContext.getSite().getHome());
								String questionPageTitle = Formatter.normalizeText(questionTitle, PROPERTIES_FORUM_TITLE_CHARS_TO_REPLACE);

								String finalQuestionPageTitle = PageHelper.getProperTitle(session, questionPageTitle,
										forumPageNode.getPath(), null);
								session.checkout(forumPageNode);
								boolean isCopied = dummyPageNode.copy(forumPageNode, finalQuestionPageTitle, false);

								if (isCopied) {
									JCRNodeWrapper questionPageNode = forumPageNode.getNode(finalQuestionPageTitle);
									questionPageNode.removeMixin("jmix:ciMarkerPageption");
									questionPageNode.setProperty("j:fullpath", questionPageNode.getPath());
									questionPageNode.setProperty("jcr:title", questionTitle);
									questionPageNode.addMixin("jmix:sitemap");
									questionPageNode.setProperty("changefreq", "weekly");
									questionPageNode.setProperty("priority", "0.5");

									JCRNodeWrapper centerAreaNode = questionPageNode.getNode("centerArea");
									String questionBody = getParameter(parameters, "body");
									questionBody = Functions.escapeXml(questionBody);
									questionBody = Formatter.convertLinksToHTML(questionBody, "", true, true, 35);
									questionPageNode.setProperty("jcr:description",StringUtils.abbreviate(questionBody,200));

									parameters.put("body", Arrays.asList(questionBody));
									JCRNodeWrapper newQuestionNode = createNode(req, parameters, centerAreaNode, "jnt:ciQuestion", "", true);
									
									newQuestionNode.setProperty("user", userNode);
									newQuestionNode.setProperty(PROPERTIES_FORUM_MODIFIED_DATE, Calendar.getInstance());
									long nbOfQuestions = GetterUtil.getLong(user.getProperty(PROPERTIES_USER_NB_OF_QUESTIONS), 0);
									user.setProperty(PROPERTIES_USER_NB_OF_QUESTIONS, Long.toString(++nbOfQuestions));
									session.save();
									return new ActionResult(HttpServletResponse.SC_OK, questionPageNode.getPath());
								}
							}
						}
						return ActionResult.OK;
					}
				});
	}

	private JCRNodeWrapper getDummyQuestionPageNode(JCRNodeWrapper home) throws RepositoryException {
		NodeIterator iterator = home.getNodes();
		JCRNodeWrapper dummyQuestionPageNode = null;
		while (iterator.hasNext()) {
			JCRNodeWrapper childNode = (JCRNodeWrapper) iterator.next();
			if ("jnt:page".equals(childNode.getPrimaryNodeTypeName()) && childNode.hasProperty(PROPERTIES_PAGE_MARKER_ISDUMMYQUESTION)
					&& "true".equals(childNode.getPropertyAsString(PROPERTIES_PAGE_MARKER_ISDUMMYQUESTION))) {
				dummyQuestionPageNode = childNode;
				break;
			}
		}
		return dummyQuestionPageNode;
	}
}
