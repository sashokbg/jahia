package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CharPool;
import org.commons.util.CiConstants;
import org.commons.util.Validator;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;

/**
 * 
 * @author el-aarko
 * @description : permet de deplacer une question d'un page thematique a une
 *              autre
 */
public class MoveQuestionAction extends Action {

	JCRTemplate jcrTemplate;

	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	public ActionResult doExecute(final HttpServletRequest req, RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		JCRUser user = (JCRUser) session.getUser();
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(user.getName(), session.getWorkspace().getName(), session.getLocale(),
				new JCRCallback<Object>() {
					public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {

						String questionPagePath = getParameter(parameters, "questionPagePath");
						String targetPagePath = getParameter(parameters, "targetPagePath");

						if (StringUtils.isNotEmpty(questionPagePath) && StringUtils.isNotEmpty(targetPagePath)) {
							JCRNodeWrapper questionPageNode = session.getNode(questionPagePath);

							JCRNodeWrapper targetPageNode = session.getNode(targetPagePath);
							NodeIterator iterator = targetPageNode.getNodes();
							String forumPagePath = null;
							while (iterator.hasNext()) {
								JCRNodeWrapper childNode = (JCRNodeWrapper) iterator.next();
								if (Constants.JAHIANT_PAGE.equals(childNode.getPrimaryNodeTypeName())
										&& childNode.hasProperty(CiConstants.PROPERTIES_PAGE_MARKER_ISFORUM)
										&& "true".equals(childNode.getPropertyAsString(CiConstants.PROPERTIES_PAGE_MARKER_ISFORUM))) {
									forumPagePath = childNode.getPath();
									break;
								}
							}
							// BL - Ano #125 : impossible de deplacer une
							// question vers une thematique ou une question
							// ayant le meme titre existe deja
							String newQuestionPagePath = forumPagePath + CharPool.SLASH
									+ PageHelper.getProperTitle(session, questionPageNode.getName(), forumPagePath, null);
							if (Validator.isNotNull(forumPagePath) && !Validator.equals(newQuestionPagePath, questionPagePath)) {
								session.move(questionPagePath, newQuestionPagePath);
								session.save();
								return new ActionResult(HttpServletResponse.SC_OK, newQuestionPagePath);
							}
						}

						return new ActionResult(HttpServletResponse.SC_OK, questionPagePath);
					}
				});
	}
}
