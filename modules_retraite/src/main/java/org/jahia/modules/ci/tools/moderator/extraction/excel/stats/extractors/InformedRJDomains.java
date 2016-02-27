package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.InformedExpertDomains;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFNumberOfMembers;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class InformedRJDomains extends StatExcelExtractor {

	public InformedRJDomains() {
		super("Retrait" + Formatter._Character.EACUTE + "s junior ayant renseign" + Formatter._Character.EACUTE
				+ " leur domaine d'expertise");
		this.setColumns(Arrays.asList(membersDoms));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {

		String name = "Domaines renseign" + Formatter._Character.EACUTE + "s";
		ICallback callback = new InformedExpertDomains();
		Integer rjDomainInformed = (Integer) callback.getData(session, renderContext);
		fillObjectValueProperties(name, rjDomainInformed);

		name = "Domaines non renseign" + Formatter._Character.EACUTE + "s";
		callback = new KFNumberOfMembers(USER_TYPE_RETRAITE_JUNIOR, ICallback.MEMBER_UNLOCKED);
		Long totalRJ = (Long) callback.getData(session, renderContext);
		fillObjectValueProperties(name, totalRJ.intValue() - rjDomainInformed);

		return null;
	}

	private static IColumn[] membersDoms = new IColumn[] { StatColumn.MEMBERS_ACTIVITY_TYPE, StatColumn.MEMBERS_ACTIVITY_VALUE };
}