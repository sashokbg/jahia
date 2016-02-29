package org.jahia.modules.ci.tools.moderator.extraction;

import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public interface ICallback {
	/** the member is locked */
	int MEMBER_LOCKED = -1;
	/** the member is not locked */
	int MEMBER_UNLOCKED = 1;
	/** the member can be locked or not */
	int MEMBER_WHATEVER_THE_LOCK = 0;

	Object getData(JCRSessionWrapper session, RenderContext renderContext);
}
