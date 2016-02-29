package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFAskedQuestions;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFFriendRecommandations;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFMarkettingOffers;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFNewsletterSite;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFNumberOfMembers;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostRated;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostedReplies;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostedRepliesFR;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostedRepliesModerators;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostedRepliesProfessional;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPostedRepliesRJ;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.kf.KFPublishedArticles;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

/**
 * Nombres cles du site (Key Figures).
 * 
 * @author foo
 * 
 */
public class KFExtractor extends StatExcelExtractor {

	public KFExtractor() {
		super("Chiffres cl" + Formatter._Character.EACUTE + "s");
		this.setColumns(Arrays.asList(kfcols));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {

		String name = "Membres du site";
		ICallback callback = new KFNumberOfMembers(null, ICallback.MEMBER_UNLOCKED);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Membres d" + Formatter._Character.EACUTE + "sinscrits";
		callback = new KFNumberOfMembers(null, ICallback.MEMBER_LOCKED);
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Questions pos" + Formatter._Character.EACUTE + "es";
		callback = new KFAskedQuestions();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "R" + Formatter._Character.EACUTE + "ponses totale apport" + Formatter._Character.EACUTE + "es";
		callback = new KFPostedReplies();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "R" + Formatter._Character.EACUTE + "ponses des professionnels";
		callback = new KFPostedRepliesProfessional();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "R" + Formatter._Character.EACUTE + "ponses des mod" + Formatter._Character.EACUTE + "rateurs";
		callback = new KFPostedRepliesModerators();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "R" + Formatter._Character.EACUTE + "ponses des futurs retrait" + Formatter._Character.EACUTE + "s";
		callback = new KFPostedRepliesFR();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "R" + Formatter._Character.EACUTE + "ponses des retrait" + Formatter._Character.EACUTE + "s juniors";
		callback = new KFPostedRepliesRJ();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Articles";
		callback = new KFPublishedArticles();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Votes";
		callback = new KFPostRated();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Recommandations";
		callback = new KFFriendRecommandations();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Inscriptions " + Formatter._Character.AGRAVE + " la newsletter";
		callback = new KFNewsletterSite();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		name = "Inscriptions aux offres commerciales";
		callback = new KFMarkettingOffers();
		fillObjectValueProperties(name, callback.getData(session, renderContext));

		return super.execute(session, renderContext);
	}

	private static IColumn[] kfcols = new IColumn[] { StatColumn.KF_NUMBERS_NAME, StatColumn.KF_NUMBERS_VALUE };
}