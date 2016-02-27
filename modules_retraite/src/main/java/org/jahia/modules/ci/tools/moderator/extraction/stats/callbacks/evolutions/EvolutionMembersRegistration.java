package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.evolutions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.jahia.api.Constants;
import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AbstractStatCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class EvolutionMembersRegistration extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EvolutionMembersRegistration.class);

	
	private String memberType;
	private int memberLock;

	public EvolutionMembersRegistration(String memberType, int memberLock) {
		this.memberType = memberType;
		this.memberLock = memberLock;
	}
	
	// date du jout
	private Date maxDate;

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		Map<Date, Integer> registrationDateMap = new HashMap<Date, Integer>();
		NodeIterator nodes = getMembersNodes(session, context, memberType, memberLock);

		if (nodes != null) {
			List<Date> dates = new ArrayList<Date>();
			while (nodes.hasNext()) {
				JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
				try {
					Date date = node.getProperty(Constants.JCR_CREATED).getDate().getTime();
					Date simplifiedDate = simplifiedDayDate(date);
					dates.add(simplifiedDate);

					maxDate = new Date();
					if ((AbstractExtractor.minDate.before(simplifiedDate) || AbstractExtractor.minDate.equals(simplifiedDate))
							&& (maxDate.after(simplifiedDate) || maxDate.equals(simplifiedDate))) {
						if (!registrationDateMap.containsKey(simplifiedDate)) {
							registrationDateMap.put(simplifiedDate, 0);
						}
						registrationDateMap.put(simplifiedDate, registrationDateMap.get(simplifiedDate) + 1);
					}

					if (LOGGER.isDebugEnabled())
						LOGGER.debug("Added new creation date in list : " + node.getName() + "\t[" + simplifiedDate + "]");
				} catch (RepositoryException e) {
					LOGGER.error("Error on getting creation date for member : " + node);
				}
			}
		}
		return registrationDateMap;
	}
}
