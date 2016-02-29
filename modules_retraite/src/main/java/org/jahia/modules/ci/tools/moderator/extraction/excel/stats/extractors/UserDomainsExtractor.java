package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.PreferedRubricsFR;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.PreferedRubricsRJ;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.PreferedThematicsRJ;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class UserDomainsExtractor extends StatExcelExtractor {

	final static String FR_RUBRICS_TITLE = "Rubriques pr" + Formatter._Character.EACUTE + "f" + Formatter._Character.EACUTE + "r"
			+ Formatter._Character.EACUTE + "es des futurs retrait" + Formatter._Character.EACUTE + "s (s" + Formatter._Character.EACUTE
			+ "lectionn" + Formatter._Character.EACUTE + "s dans leur profil)";
	final static String RJ_RUBRICS_TITLE = "Rubriques pr" + Formatter._Character.EACUTE + "f" + Formatter._Character.EACUTE + "r"
			+ Formatter._Character.EACUTE + "es des retrait" + Formatter._Character.EACUTE + "s juniors (s" + Formatter._Character.EACUTE
			+ "lectionn" + Formatter._Character.EACUTE + "s dans leur profil)";
	final static String RJ_DOMAIN_TITLE = "Domaines d'expertise choisi par les retrait" + Formatter._Character.EACUTE + "s juniors";

	final static String FR = "fr";
	final static String RJ = "rj";
	final static String EXPERT_DOMAINS = "expertDomains";

	public UserDomainsExtractor() {
		super("");
		this.setColumns(Arrays.asList(siteDomains));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		ICallback callback = null;
		if (statParam.equals(FR)) {
			setTitle(FR_RUBRICS_TITLE);
			StatColumn.SITE_DOMAINS_NAME.setLabel(FR_RUBRICS_TITLE);
			callback = new PreferedRubricsFR();
		}
		if (statParam.equals(RJ)) {
			setTitle(RJ_RUBRICS_TITLE);
			StatColumn.SITE_DOMAINS_NAME.setLabel(RJ_RUBRICS_TITLE);
			callback = new PreferedRubricsRJ();
		}
		if (statParam.equals(EXPERT_DOMAINS)) {
			setTitle(RJ_DOMAIN_TITLE);
			StatColumn.SITE_DOMAINS_NAME.setLabel(RJ_DOMAIN_TITLE);
			callback = new PreferedThematicsRJ();
		}
		if (callback != null)
			populateWithMapCallback(session, renderContext, callback);
		
		return null;
	}

	
	
	private static IColumn[] siteDomains = new IColumn[] { StatColumn.SITE_DOMAINS_NAME, StatColumn.SITE_DOMAINS_VALUE };

}