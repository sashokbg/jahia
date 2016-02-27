package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.RegionsOfMembers;
import org.jahia.modules.ci.tools.moderator.extraction.stats.localities.Region;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class RegionExtractor extends StatExcelExtractor {

	public RegionExtractor() {
		super("R" + Formatter._Character.EACUTE + "partition des membres par r" + Formatter._Character.EACUTE + "gion");
		this.setColumns(Arrays.asList(membersRegions));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		populateWithMapCallback(session, renderContext, new RegionsOfMembers());
		return null;
	}

	@Override
	protected void fillObjectValueProperties(Object entry, Object value) {
		super.fillObjectValueProperties(((Region) entry).getLabel(), value);
	}

	private static IColumn[] membersRegions = new IColumn[] { StatColumn.MEMBERS_REGION_NAME, StatColumn.MEMBERS_REGION_VALUE };
}