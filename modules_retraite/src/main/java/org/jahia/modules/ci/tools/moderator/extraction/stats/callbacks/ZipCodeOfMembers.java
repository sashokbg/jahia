package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

public class ZipCodeOfMembers extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ZipCodeOfMembers.class);

	public String HORS_FRANCE = "Hors France";

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		Map<String, Integer> memberZipTempMap = new HashMap<String, Integer>();
		Map<String, Integer> memberZipEndMap = new HashMap<String, Integer>();
		NodeIterator nodes = getMembersNodes(session, context, null, MEMBER_UNLOCKED);

		while (nodes.hasNext()) {
			JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();

			String userZipCode = null;
			try {
				if (node.hasProperty(PARAM_USER_ZIPCODE)) {
					userZipCode = node.getProperty(PARAM_USER_ZIPCODE).getString();
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("Added new zip in list : " + node.getName() + "[" + userZipCode + "]");
					String zipDept = userZipCode.substring(0, 2);
					if (Integer.parseInt(zipDept) >= 97)
						zipDept = userZipCode.substring(0, 3);
					if (!memberZipTempMap.containsKey(zipDept)) {
						memberZipTempMap.put(zipDept, 0);
					}
					if (zipDept != null && !zipDept.equals("")) {
						memberZipTempMap.put(zipDept, memberZipTempMap.get(zipDept) + 1);
					}
				} else {
					if (!memberZipTempMap.containsKey(HORS_FRANCE)) {
						memberZipTempMap.put(HORS_FRANCE, 0);
					}
					memberZipTempMap.put(HORS_FRANCE, memberZipTempMap.get(HORS_FRANCE) + 1);
				}

			} catch (RepositoryException e) {
				LOGGER.error("Error on getting zip for member : " + node);
			}

		}
		// ordonne la map
		Set<String> cpKeySet = new TreeSet<String>(Collections.reverseOrder(new ValueComparator(memberZipTempMap)));
		cpKeySet.addAll(memberZipTempMap.keySet());
		// creer la liste
		for (String cp : cpKeySet) {
			memberZipEndMap.put(cp, memberZipTempMap.get(cp));
		}

		// retour de la map
		return memberZipEndMap;
	}

	// ordonne une map
	static class ValueComparator implements Comparator<Object> {
		private Map<? extends Object, Integer> map;

		public ValueComparator(Map<? extends Object, Integer> base) {
			this.map = base;
		}

		public int compare(Object a, Object b) {
			if (map.get(a).equals(map.get(b))) {
				if (a.equals(b)) {
					return 0;
				} else {
					return String.valueOf(b).compareTo(String.valueOf(a));
				}
			}
			return (Integer) map.get(a).compareTo((Integer) map.get(b));
		}

		@Override
		public boolean equals(Object arg0) {
			return super.equals(arg0);
		}
	}

}
