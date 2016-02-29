package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFNumberOfMembers;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class MembersTypeExtractor extends StatExcelExtractor {
	public static final String FUTURE_RETIRED = "futureRetired";
	public static final String RETIRED = "retired";

	public MembersTypeExtractor() {
		super("R" + Formatter._Character.EACUTE + "partition des membres par type");
		this.setColumns(Arrays.asList(columns));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {

		String name = "Nombre de futurs retrait" + Formatter._Character.EACUTE + "s";
		ICallback callback = new KFNumberOfMembers(USER_TYPE_FUTUR_RETRAITE, ICallback.MEMBER_UNLOCKED);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Nombre de retrait" + Formatter._Character.EACUTE + "s juniors";
		callback = new KFNumberOfMembers(USER_TYPE_RETRAITE_JUNIOR, ICallback.MEMBER_UNLOCKED);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		return null;
	}

	private static IColumn[] columns = new IColumn[] { StatColumn.MEMBERS_TYPE_NAME, StatColumn.MEMBERS_TYPE_VALUE };

}