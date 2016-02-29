package org.jahia.taglibs.ci.errors;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.settings.SettingsBean;
import org.jahia.taglibs.AbstractJahiaTag;
import org.jahia.utils.JahiaTools;

@SuppressWarnings("serial")
public class ErrorPageTag extends AbstractJahiaTag {

	/**
	 * logger
	 */
	private static final Log LOGGER = LogFactory.getLog(ErrorPageTag.class);

	private Integer errorNumber;
	private String var = "errorPage";
	private static final String LOCAL_ADDRESS = "localAddress";
	private static final String LOCAL_PORT = "localPort";

	public void setErrorNumber(Integer errorNumber) {
		this.errorNumber = errorNumber;
	}

	public void setVar(String var) {
		this.var = var;
	}

	@Override
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = request.getSession();
		String siteKey = (String) request.getAttribute("jahiaSiteKeyForCurrentServerName");

		if (StringUtils.isNotEmpty(siteKey)) {
			JahiaUser currentUser = getUser();

			if (currentUser == null) {
				if (session.getAttribute("org.jahia.usermanager.jahiauser") != null)
					currentUser = (JahiaUser) session.getAttribute("org.jahia.usermanager.jahiauser");
				else
					currentUser = ServicesRegistry.getInstance().getJahiaUserManagerService().lookupUser("guest");
			}

			if (currentUser != null) {

				JahiaUser root = ServicesRegistry.getInstance().getJahiaUserManagerService().lookupUser("root");

				ServicesRegistry.getInstance().getJCRStoreService().getSessionFactory().setCurrentUser(root);
				JCRSessionWrapper jcrSession;

				try {
					jcrSession = ServicesRegistry.getInstance().getJCRStoreService().getSessionFactory().getCurrentUserSession();
					String errorPagePath = "/sites/" + siteKey + "/" + errorNumber;
					if (jcrSession.nodeExists(errorPagePath)) {
					JCRNodeWrapper errorPageNode = jcrSession.getNode(errorPagePath);

					if (LOGGER.isInfoEnabled())
						LOGGER.info("Found custom " + errorNumber + " site page : " + errorPageNode);

					String localAddress = StringUtils.defaultIfEmpty(
							SettingsBean.getInstance().getPropertiesFile().getProperty(LOCAL_ADDRESS), "localhost");
					String localPort = StringUtils.defaultIfEmpty(SettingsBean.getInstance().getPropertiesFile().getProperty(LOCAL_PORT),
							"8080");

						String jahiaPath = request.getScheme() + "://" + localAddress + ":" + localPort + "/cms";
						String url = jahiaPath + errorPagePath + ".html";

						if (LOGGER.isInfoEnabled())
							LOGGER.info("Requesting for " + errorNumber + " page : " + url);

						String pageContent = JahiaTools.getPage(url, currentUser);

						if (errorNumber != 404) {
							URLResolver urlResolver = (URLResolver) request.getAttribute("urlResolver");
							Source source = new Source(pageContent);
							StartTag inputRedirect = source.getFirstStartTag("name", Pattern.compile("redirect"));
							String redirect = inputRedirect.getAttributeValue("value");
							pageContent = pageContent
									.replace(redirect, "/cms/" + request.getLocale().getLanguage() + urlResolver.getPath());
						}

						request.setAttribute(var, pageContent);
					}

				} catch (Exception e) {
					throw new JspException("Error when trying to retrieve url for error page : " + errorNumber, e);
				} finally {
					ServicesRegistry.getInstance().getJCRStoreService().getSessionFactory().setCurrentUser(currentUser);
				}
			}
		}

		return SKIP_BODY;
	}

}
