package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.AgeOfMembers;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class MembersAgesExtractor extends StatExcelExtractor {
	public static final String AGE = "age";

	public MembersAgesExtractor() {
		super("R" + Formatter._Character.EACUTE + "partition des membres par tranche d'" + Formatter._Character.ACIRC + "ge");
		this.setColumns(Arrays.asList(membersType));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		populateWithMapCallback(session, renderContext, new AgeOfMembers());
		return null;
	}

	private static IColumn[] membersType = new IColumn[] { StatColumn.MEMBERS_AGES_NAME, StatColumn.MEMBERS_AGES_VALUE };
}