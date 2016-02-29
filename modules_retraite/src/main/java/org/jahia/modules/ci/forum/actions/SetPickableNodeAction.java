package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.jahia.taglibs.jcr.node.JCRTagUtils;

/**
 * 
 * US-1200
 * 
 */
public class SetPickableNodeAction extends Action implements CiConstants {

	JCRTemplate jcrTemplate;

	public void setJcrTemplate(JCRTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

	@Override
	public ActionResult doExecute(final HttpServletRequest req, final RenderContext renderContext, final Resource resource,
			JCRSessionWrapper session, final Map<String, List<String>> parameters, final URLResolver urlResolver) throws Exception {
		JCRUser user = (JCRUser) session.getUser();

		final String _picker;
		final String _displayTab;
		final String moderatorToolsFlag = getParameter(parameters, PARAM_PICKABLE_MODERATOR_TOOLS_FLAG);
		if (StringUtils.isNotEmpty(moderatorToolsFlag) && moderatorToolsFlag.equals("true")) {
			_picker = PROPERTIES_PICKABLE_IS_NOT_TOOLS;
			_displayTab = getParameter(parameters, "displayTab");
		} else {
			_picker = PROPERTIES_PICKABLE_IS_NOT_PICKABLE;
			_displayTab = getParameter(parameters, "displayTab", "");
		}
		return (ActionResult) jcrTemplate.doExecuteWithSystemSession(user.getName(), session.getWorkspace().getName(), session.getLocale(),
				new JCRCallback<Object>() {
					public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
						String nodePath = getParameter(parameters, PARAM_PICKABLE_NODE_PATH);
						JCRNodeWrapper postNode = session.getNode(nodePath);
						switchPickablePost(postNode, _picker);
						JCRNodeWrapper pageNode;

						if (!urlResolver.getNode().isNodeType("jnt:page"))
							pageNode = JCRTagUtils.getParentOfType(urlResolver.getNode(), "jnt:page");
						else
							pageNode = urlResolver.getNode();
						String url = pageNode.getAbsoluteUrl(req)
								+ (StringUtils.isNotEmpty(_displayTab) ? "?displayTab=" + _displayTab : "");
						return new ActionResult(HttpServletResponse.SC_OK, url, true, null);
					}
				});
	}

	private boolean switchPickablePost(JCRNodeWrapper pickableNode, String picker) {
		boolean switched = false;
		final String JMIX_PICKABLE = "jmix:ciPickable";
		final String JMIX_TOOLS_PICKABLE = "jmix:ciToolsPickable";
		try {
			String primaryNodeType = pickableNode.getPrimaryNodeTypeName();
			if ((PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE.equals(primaryNodeType) || PROPERTIES_FORUM_REPLY_PRIMARY_TYPE
					.equals(primaryNodeType))
					&& picker.equals(PROPERTIES_PICKABLE_IS_NOT_PICKABLE)
					&& !pickableNode.isNodeType(JMIX_PICKABLE)) {
				pickableNode.addMixin(JMIX_PICKABLE);
			}
			if ((PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE.equals(primaryNodeType) || PROPERTIES_FORUM_REPLY_PRIMARY_TYPE
					.equals(primaryNodeType))
					&& picker.equals(PROPERTIES_PICKABLE_IS_NOT_TOOLS)
					&& !pickableNode.isNodeType(JMIX_TOOLS_PICKABLE)) {
				pickableNode.addMixin(JMIX_TOOLS_PICKABLE);
			}
			Property pickableProperty = pickableNode.getProperty(picker);
			boolean isPickable = pickableProperty != null && pickableProperty.getBoolean();
			pickableNode.setProperty(picker, !isPickable);
			pickableNode.saveSession();
			switched = true;
		} catch (NoSuchNodeTypeException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return switched;
	}

}
