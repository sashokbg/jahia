package org.jahia.modules.ci.tools.moderator.extraction;


import org.commons.util.CiConstants;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public interface IExtractor extends CiConstants {

	Object execute(JCRSessionWrapper session, RenderContext renderContext);

	void setStatParam(String param);

}
