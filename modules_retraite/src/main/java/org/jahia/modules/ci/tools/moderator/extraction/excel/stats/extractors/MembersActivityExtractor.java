package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.MembersStatus;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class MembersActivityExtractor extends StatExcelExtractor {

	public MembersActivityExtractor() {
		super("R" + Formatter._Character.EACUTE + "partition des membres par activit" + Formatter._Character.EACUTE);
		this.setColumns(Arrays.asList(membersActivity));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {

		String name = "Salari" + Formatter._Character.EACUTE + "s";
		ICallback callback = new MembersStatus(USER_ACTIVITY_SALARIE);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Travailleurs non salari" + Formatter._Character.EACUTE + "s";
		callback = new MembersStatus(USER_ACTIVITY_NON_SALARIE);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Fonctionnaires";
		callback = new MembersStatus(USER_ACTIVITY_FONCTIONNAIRE);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Non renseign" + Formatter._Character.EACUTE + "s";
		callback = new MembersStatus(null);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		return null;
	}

	private static IColumn[] membersActivity = new IColumn[] { StatColumn.MEMBERS_ACTIVITY_TYPE, StatColumn.MEMBERS_ACTIVITY_VALUE };
}