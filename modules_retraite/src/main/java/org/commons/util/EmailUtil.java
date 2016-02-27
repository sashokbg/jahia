package org.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.mail.MailService;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utile sur la gestion d'envoie de mail
 * 
 * @author Baroin
 */
public class EmailUtil {

	
	
	private static MailService mailService;

	// Envoie de mail
	public static void sendMail(String from, String to, String subject, String htmlBody, String[] searchList, String[] replacementList,
			RenderContext renderContext) {
		mailService = ServicesRegistry.getInstance().getMailService();
		// replace Tokens with real values
		htmlBody = StringUtils.replaceEach(htmlBody, searchList, replacementList);
		// ajout de la balise html
		htmlBody = "<html><body>" + htmlBody + "</body></html>";
		htmlBody = cleanHtmlForNewsletter(renderContext, htmlBody, true);
		// send mail
		try {
			mailService.sendHtmlMessage(from, to, "", "", subject, htmlBody);
		} catch (Throwable t) {
			_log.trace("Erreur lors de l'envoi de mail", t);
		}
	}
	
	// Envoie de mail
		public static void sendMail(String from, String to,String bcc, String subject, String htmlBody, String[] searchList, String[] replacementList,
				RenderContext renderContext) {
			mailService = ServicesRegistry.getInstance().getMailService();
			// replace Tokens with real values
			htmlBody = StringUtils.replaceEach(htmlBody, searchList, replacementList);
			// ajout de la balise html
			htmlBody = "<html><body>" + htmlBody + "</body></html>";
			htmlBody = cleanHtmlForNewsletter(renderContext, htmlBody, true);
			// send mail
			try {
				mailService.sendHtmlMessage(from, to, "", bcc, subject, htmlBody);
			} catch (Throwable t) {
				_log.trace("Erreur lors de l'envoi de mail", t);
			}
		}

	private static String cleanHtmlForNewsletter(RenderContext ctx, String content, boolean includecss) {
		if ((content == null) || (content.length() == 0)) {
			return "";
		}

		String remoteBaseHref = "http://" + ctx.getSite().getServerName() + ":" + ctx.getRequest().getServerPort();
		String localBaseHref = "http://" + ctx.getRequest().getLocalAddr() + ":" + ctx.getRequest().getServerPort();

		Source source = new Source(content);
		OutputDocument document = new OutputDocument(source);
		List<Element> linkTags = source.getAllElements("a");
		for (Element startTag : linkTags) {
			Attributes attributes = startTag.getAttributes();
			Attribute href = attributes.get("href");
			restoreURL(ctx, document, href);
		}
		linkTags = source.getAllElements("img");
		for (Element startTag : linkTags) {
			Attributes attributes = startTag.getAttributes();
			Attribute href = attributes.get("src");
			restoreURL(ctx, document, href);
		}
		linkTags = source.getAllElements("input");
		for (Element startTag : linkTags) {
			Attributes attributes = startTag.getAttributes();
			Attribute href = attributes.get("src");
			restoreURL(ctx, document, href);
		}

		StringBuilder sb = new StringBuilder();
		linkTags = source.getAllElements("link");
		for (Element startTag : linkTags) {
			Attributes attributes = startTag.getAttributes();
			if ("stylesheet".equals(attributes.get("rel").getValue())) {
				if (includecss) {
					Attribute type = attributes.get("type");

					String href = attributes.get("href").getValue();
					if (href == null) {
						continue;
					}
					Attribute media = attributes.get("media");
					if ((media != null) && (!(media.getValue().equalsIgnoreCase("screen")))) {
						continue;
					}
					try {
						InputStream is = new URL(localBaseHref + href).openStream();

						StringWriter baos = new StringWriter();
						IOUtils.copy(is, baos);

						sb.setLength(0);
						sb.append("<style");
						if (type != null) {
							sb.append(' ').append(type);
						}
						String baseHref = href;
						if (href.startsWith("/")) {
							baseHref = remoteBaseHref + href;
						}
						String basehref = baseHref.substring(0, baseHref.lastIndexOf(47) + 1);

						String stylesheet = baos.toString();

						stylesheet = Pattern.compile("url *\\( *\"?([^\\:\" )]*)\"? *\\)").matcher(stylesheet)
								.replaceAll("url(\"" + basehref + "$1\")");

						sb.append(">\n").append(stylesheet).append("\n</style>");
						document.replace(startTag, sb.toString());
					} catch (IOException e) {
						_log.error("Cannot get stylesheet part", e);
					}
				} else {
					document.replace(startTag, "");
				}
			}
		}

		linkTags = source.getAllElements("div");
		for (Element startTag : linkTags) {
			Attributes attributes = startTag.getAttributes();
			Attribute styleAttr = attributes.get("style");
			if (styleAttr != null) {
				String v = styleAttr.getValue();
				v = Pattern.compile("url *\\( *\"?([^\\:\")]*)\"? *\\)").matcher(v).replaceAll("url(" + remoteBaseHref + "$1)");
				document.replace(styleAttr.getValueSegment(), v);
			}

		}

		return document.toString();
	}

	private static void restoreURL(RenderContext ctx, OutputDocument document, Attribute href) {
		if (href == null) {
			return;
		}
		String originalHrefValue = href.getValue();
		String hrefValue = originalHrefValue;
		if (hrefValue.startsWith("/")) {
			hrefValue = "http://" + ctx.getSite().getServerName() + ":" + ctx.getRequest().getServerPort() + hrefValue;
			hrefValue = hrefValue.replaceAll("\\{mode\\}", "render/live");
			hrefValue = hrefValue.replaceAll("\\{lang\\}", ctx.getMainResourceLocale().getLanguage());
			if (hrefValue.contains(";jsessionid")) {
				hrefValue = hrefValue.substring(0, hrefValue.indexOf(";jsessionid"));
			}
			if ((hrefValue != null) && (!(originalHrefValue.equals(hrefValue))))
				document.replace(href.getValueSegment(), hrefValue);
		}
	}

	private transient static Logger _log = LoggerFactory.getLogger(MailService.class);
}