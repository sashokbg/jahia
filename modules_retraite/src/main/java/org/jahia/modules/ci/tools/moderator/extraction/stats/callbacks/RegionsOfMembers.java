package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jahia.modules.ci.tools.moderator.extraction.stats.localities.Department;
import org.jahia.modules.ci.tools.moderator.extraction.stats.localities.Region;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class RegionsOfMembers extends AbstractStatCallback {

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext renderContext) {
		@SuppressWarnings("unchecked")
		Map<String, Integer> zipMap = new TreeMap<String, Integer>((Map<String, Integer>) new ZipCodeOfMembers().getData(session,
				renderContext));
		Map<Region, Integer> regionMap = new HashMap<Region, Integer>();

		for (String dept : zipMap.keySet()) {
			Department deptEnum = Department.getFromDepartmentNumber(dept);
			if (deptEnum != null) {
				Integer zipCount = zipMap.get(dept);
				int regionCount = 0;
				if (regionMap.containsKey(deptEnum.getRegion())) {
					regionCount += regionMap.get(deptEnum.getRegion());
				}
				regionCount += zipCount;
				regionMap.put(deptEnum.getRegion(), regionCount);
			}
		}
		return regionMap;
	}
}
