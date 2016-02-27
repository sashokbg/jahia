package org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * "Chiffres cles" extraction on JSON format.
 * 
 * @author foo
 * 
 */
public class KeyNumber extends AbstractExtractor {
	final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KeyNumber.class);

	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray();

			JSONArray json = null;
			ICallback callback = null;

			// Nombre de membre
			callback = new KFNumberOfMembers(null, ICallback.MEMBER_UNLOCKED);
			json = new JSONArray();
			json.put("Nombre de membres");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de membre
			callback = new KFNumberOfMembers(null, ICallback.MEMBER_LOCKED);
			json = new JSONArray();
			json.put("Nombre de d\u00E9sinscrits");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Recuperation des questions
			callback = new KFAskedQuestions();
			json = new JSONArray();
			json.put("Nombre de questions pos" + Formatter._Character.EACUTE + "es");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de reponse du site
			callback = new KFPostedReplies();
			json = new JSONArray();
			json.put("Nombre de r" + Formatter._Character.EACUTE + "ponses total");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de reponse des pros
			callback = new KFPostedRepliesProfessional();
			json = new JSONArray();
			json.put("Nombre de r" + Formatter._Character.EACUTE + "ponses des professionnels");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de reponse des modos
			callback = new KFPostedRepliesModerators();
			json = new JSONArray();
			json.put("Nombre de r" + Formatter._Character.EACUTE + "ponses du mod" + Formatter._Character.EACUTE + "rateur");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de reponse des FR
			callback = new KFPostedRepliesFR();
			json = new JSONArray();
			json.put("Nombre de r" + Formatter._Character.EACUTE + "ponses des futurs retrait" + Formatter._Character.EACUTE
					+ "s");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de reponse des RJ
			callback = new KFPostedRepliesRJ();
			json = new JSONArray();
			json.put("Nombre de r" + Formatter._Character.EACUTE + "ponses des retrait" + Formatter._Character.EACUTE
					+ "s juniors");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre de vote
			callback = new KFPostRated();
			json = new JSONArray();
			json.put("Nombre de votes");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Recuperation des articles
			callback = new KFPublishedArticles();
			json = new JSONArray();
			json.put("Nombre d'articles");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre d'inscrits à la newsletter
			// membre
			callback = new KFNewsletterSite();
			json = new JSONArray();
			json.put("Nombre d'inscrits " + Formatter._Character.AGRAVE + " la newsletter");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Nombre d'inscrits aux offres du groupe
			callback = new KFMarkettingOffers();
			json = new JSONArray();
			json.put("Nombre d'inscrits aux offres commerciales");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			// Recommandation
			callback = new KFFriendRecommandations();
			json = new JSONArray();
			json.put("Nombre de recommandations");
			json.put(callback.getData(session, renderContext));
			array.put(json);

			result.put("rows", array);
		} catch (JSONException e) {
			LOGGER.error("Unable to load json array on result...");
		}

		// retour de donnees
		return result;
	}

}