package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf;

import javax.jcr.NodeIterator;

import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class KFNumberOfMembers extends AbstractStatCallback {

	private String memberType;
	private int memberLock;

	public KFNumberOfMembers(String memberType, int memberLock) {
		this.memberType = memberType;
		this.memberLock = memberLock;
	}

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		NodeIterator nodes = getMembersNodes(session, context, memberType, memberLock);
		return nodes.getSize();
	}

}
