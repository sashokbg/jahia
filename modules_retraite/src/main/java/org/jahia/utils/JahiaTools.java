package org.jahia.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jahia.api.Constants;
import org.jahia.exceptions.JahiaException;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.params.valves.TokenAuthValveImpl;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.settings.SettingsBean;

import java.io.*;
import java.net.URL;

public class JahiaTools {

    private static final Log LOGGER = LogFactory.getLog(JahiaTools.class);
    private static FileCleaningTracker fileCleaningTracker = new FileCleaningTracker();

    /**
     * @throws IOException
     * @throws JahiaException
     */
    public static String getPage(String pageUrl, JahiaUser user) throws IOException, JahiaException {
        URL url = new URL(pageUrl);
        InputStream is = (InputStream) JahiaTools.getResponse(url, user, 5, false);
        StringWriter out = new StringWriter();
        IOUtils.copy(is, out, SettingsBean.getInstance().getCharacterEncoding());
        return out.toString();
    }

    private static Object getResponse(URL url, JahiaUser user, int redirectContinue, boolean asString) throws IOException {
        GetMethod method;

		/*
         * ANO#138 : pbm de cache dans la page erreur 404
		 * 
		 * Note du dev : la variable httpClient fut static jadis
		 */
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setMaxTotalConnections(40);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(10);

        HttpClient httpClient = new HttpClient(connectionManager);
        httpClient.getParams().setConnectionManagerTimeout(3000);
        httpClient.getParams().setCookiePolicy("compatibility");

        for (; redirectContinue > 0; --redirectContinue) {
            method = new GetMethod(url.toString());
            method.setFollowRedirects(false);

            if (!Constants.GUEST_USERNAME.equals(user.getUsername()))
                method.addRequestHeader("jahiatoken", TokenAuthValveImpl.addToken(user));

            int result = httpClient.executeMethod(method);
            LOGGER.debug("Response status code for [" + url + "] is : " + result);

            if (result == 302) {
                Header locationHeader = method.getResponseHeader("location");
                if (locationHeader != null) {
                    String redirectLocation = locationHeader.getValue();
                    url = new URL(redirectLocation);
                    method.releaseConnection();
                    LOGGER.info("Redirection to a different host [" + url + "]");
                } else {
                    method.releaseConnection();
                    throw new IOException("No HTTP location Header in redirect send by request to [" + url + "]. HTTP status code ["
                            + result + "]");
                }
            } else {
                if (result != 200) {
                    method.releaseConnection();
                    throw new HttpException("Unsupported HTTP status code [" + result + " i.e. " + method.getStatusText()
                            + "] for request to [" + url + "].");
                }
                if (asString) {
                    String output = method.getResponseBodyAsString();
                    method.releaseConnection();
                    return output;
                }
                DeferredFileOutputStream dfos = new DeferredFileOutputStream(10485760, File.createTempFile("temp", "httpclient"));

                InputStream inputStream = new BufferedInputStream(new FileInputStream(dfos.getFile()));
                fileCleaningTracker.track(dfos.getFile(), inputStream);
                IOUtils.copy(method.getResponseBodyAsStream(), dfos);
                method.releaseConnection();
                if (dfos.isInMemory()) {
                    inputStream.close();
                    return new ByteArrayInputStream(dfos.getData());
                }
                return inputStream;
            }

        }

        throw new IOException("Too many redirects for request to [" + url + "].");
    }

    public static String getUrl(String siteAddress, JCRNodeWrapper node, String noteType) {
        String serverName = siteAddress.replaceFirst("http://", "").replaceFirst("https://", ""); //remove hardcoded http(s)
        serverName = SettingsBean.getInstance().getPropertiesFile().getProperty("serverScheme", "https")
                + "://" + serverName; //add parameter http(s) from jahia.properties

        String relativeUrl = PageHelper.getParentOfType(node, noteType).getUrl();
        relativeUrl = relativeUrl.replaceFirst("/cms/render/live/fr/sites/preparonsmaretraite", "");

        return serverName + relativeUrl;
    }

    public static String clearUrl(String siteAddress, JCRNodeWrapper node) {
        String serverName = siteAddress.replaceFirst("http://", "").replaceFirst("https://", ""); //remove hardcoded http(s)
        serverName = SettingsBean.getInstance().getPropertiesFile().getProperty("serverScheme", "https")
                + "://" + serverName; //add parameter http(s) from jahia.properties

        String relativeUrl = node.getUrl();
        relativeUrl = relativeUrl.replaceFirst("/cms/render/live/fr/sites/preparonsmaretraite", "");

        return serverName + relativeUrl;
    }
}