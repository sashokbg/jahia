package org.jahia.modules.ci.tools.moderator.extraction.excel.stats;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.helpers.ModeratorToolsHelper;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.DepartmentExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.InformedRJDomains;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.KFExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.MembersActivityExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.MembersAgesExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.MembersTypeExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.RegionExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.SiteThemesExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.UserDomainsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics.SiteInscriptionsExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors.periodics.SitePostsExtractor;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;

/**
 * 
 * @author foo
 * 
 */
public class StatsExcelAction extends Action implements CiConstants {

	private transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StatsExcelAction.class);

	@Override
	public ActionResult doExecute(HttpServletRequest req, final RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		Date date = new Date();

		final String siteKey = renderContext.getSite().getSiteKey();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(siteKey + " - Extracting site \"" + renderContext.getSite().getName() + "\" statistics starting ...");
		}

		// loading newsletter subscribers
		String extractorParam = getParameter(parameters, "extractor", "");
		String statParam = getParameter(parameters, "statParam", "");
		
		StatExcelExtractor extractor = null;

		try {
			final ExtractorClasses extractorClass = ExtractorClasses.valueOf(extractorParam);
			extractor = (StatExcelExtractor) extractorClass.getClassName().newInstance();
		} catch (Exception e) {
			LOGGER.error("Cannot instanciate extractor class " + extractorParam + " : " + e.getCause());
		}

		if (extractor != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(siteKey + " - Requested extractor \"" + extractor.getTitle() + "...");
			}

			final StatExcelExtractor finalExtractor = extractor;

			if (StringUtils.isNotEmpty(statParam))
				finalExtractor.setStatParam(statParam);
			
			synchronized (finalExtractor) {
				JCRTemplate.getInstance().doExecuteWithSystemSession(getCurrentUser().getName(), Constants.LIVE_WORKSPACE,
						session.getLocale(), new JCRCallback<Object>() {
							public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
								finalExtractor.execute(session, renderContext);
								return null;
							}
						});
			}

			ModeratorToolsHelper.writeWorkbook(renderContext, date, finalExtractor);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(siteKey + " - Extraction finished in " + (new Date().getTime() - date.getTime()) + "ms.");
			}
		}

		return ActionResult.OK;
	}

	protected enum ExtractorClasses {

		UserDomains(UserDomainsExtractor.class), //
		SiteThemes(SiteThemesExtractor.class), //
		SitePosts(SitePostsExtractor.class), //
		SiteInscriptions(SiteInscriptionsExtractor.class), //
		MembersActivity(MembersActivityExtractor.class), //
		MembersType(MembersTypeExtractor.class), //
		MembersAge(MembersAgesExtractor.class), //
		InformedRJDomains(InformedRJDomains.class), //
		RegionExtractor(RegionExtractor.class), //
		DepartmentExtractor(DepartmentExtractor.class), //
		KeyFigure(KFExtractor.class);

		private Class<?> className;

		ExtractorClasses(Class<?> className) {
			this.className = className;
		}

		Class<?> getClassName() {
			return className;
		}

	}

}
