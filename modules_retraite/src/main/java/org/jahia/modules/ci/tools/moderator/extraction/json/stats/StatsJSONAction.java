package org.jahia.modules.ci.tools.moderator.extraction.json.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.tools.moderator.extraction.IExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.AgeMemberExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.InformedExpertDomainsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.KeyNumber;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.MembersStatusExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.MembersTypeExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.MostRubricAndThematic;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.MostViewedRubricsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.MostViewedTHematicsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.RegionExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.RubricsFutRet;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.RubricsRetJunior;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.ThematicsRetJunior;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.ZipCodeExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.periodics.MembersRegistration;
import org.jahia.modules.ci.tools.moderator.extraction.json.stats.extractors.periodics.QuestionsCount;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;

public class StatsJSONAction extends Action implements CiConstants {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StatsJSONAction.class);
	private static Map<String, JSONObject> statsMap = new HashMap<String, JSONObject>();

	@Override
	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

		final String siteKey = renderContext.getSite().getSiteKey();

		// recuperation du type de stats a afficher
		String typeStat = getParameter(parameters, "typeStat", "");
		String statParam = getParameter(parameters, "statParam", "");
		String cacheKey = typeStat + "-" + statParam;

		// Evolution des inscriptions

		if (typeStat.equals("reload")) {
			statsMap.clear();
			return ActionResult.OK_JSON;
		}

		if (statsMap.containsKey(cacheKey)) {
			return new ActionResult(HttpServletResponse.SC_OK, null, statsMap.get(cacheKey));
		}

		Date date = new Date();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(siteKey + " - Extracting site \"" + renderContext.getSite().getName() + "\" statistics starting ...");
		}
		IExtractor statExtractor = null;
		JSONObject json = null;

		try {
			final ExtractorClasses extractorClass = ExtractorClasses.valueOf(typeStat);
			statExtractor = (IExtractor) extractorClass.getClassName().newInstance();
		} catch (Exception e) {
			LOGGER.error("Cannot instanciate extractor class " + typeStat + " : " + e.getCause());
			return ActionResult.INTERNAL_ERROR_JSON;
		}

		if (statExtractor != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(siteKey + " - Requested extractor \"" + statExtractor.getClass().getName() + "...");
			}

			final IExtractor finalExtractor = statExtractor;

			if (StringUtils.isNotEmpty(statParam))
				finalExtractor.setStatParam(statParam);

			synchronized (finalExtractor) {
				json = JCRTemplate.getInstance().doExecuteWithSystemSession(getCurrentUser().getName(), Constants.LIVE_WORKSPACE,
						session.getLocale(), new JCRCallback<JSONObject>() {
							public JSONObject doInJCR(JCRSessionWrapper session) throws RepositoryException {
								return (JSONObject) finalExtractor.execute(session, renderContext);
							}
						});
				statsMap.put(cacheKey, json);
			}
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(siteKey + " - Extraction finished in " + (new Date().getTime() - date.getTime()) + "ms.");
			}
		}

		return new ActionResult(HttpServletResponse.SC_OK, null, json);

	}

	protected enum ExtractorClasses {

		MemberRegistration(MembersRegistration.class), //
		KeyNumber(KeyNumber.class), //
		MostRubricAndThematic(MostRubricAndThematic.class), //
		QuestionsCount(QuestionsCount.class), //
		ThematicsRetJunior(ThematicsRetJunior.class), //
		RubricsFutRet(RubricsFutRet.class), //
		RubricsRetJunior(RubricsRetJunior.class), //
		ZipCodeExtractor(ZipCodeExtractor.class), //
		RegionExtractor(RegionExtractor.class), //
		MostViewedRubrics(MostViewedRubricsExtractor.class), //
		MostViewedTHematics(MostViewedTHematicsExtractor.class), //
		InformedExpertDomains(InformedExpertDomainsExtractor.class), //
		MembersType(MembersTypeExtractor.class), //
		MembersStatus(MembersStatusExtractor.class), //
		AgeMemberExtractor(AgeMemberExtractor.class);

		private Class<?> className;

		ExtractorClasses(Class<?> className) {
			this.className = className;
		}

		Class<?> getClassName() {
			return className;
		}

	}

}
