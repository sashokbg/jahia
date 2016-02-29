package org.commons.util;

import net.htmlparser.jericho.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTools {

    /**
     * @param mailContent
     */
    public static String cleanHtmlForNewsletter(String mailContent, String excludedIds) {
        Source source = new Source(mailContent);
        OutputDocument document = new OutputDocument(source);

        List<StartTag> linkTags = (ArrayList<StartTag>) source.getAllStartTags("link");
        for (StartTag startTag : linkTags) {
            Attributes attributes = startTag.getAttributes();
            if (attributes.get("id") == null || !attributes.get("id").getValue().equals(excludedIds)) {
                Element element = startTag.getElement();
                document.remove(new Segment(source, element.getBegin(), element.getEnd()));
            }
        }
        linkTags = (ArrayList<StartTag>) source.getAllStartTags("script");
        for (StartTag startTag : linkTags) {
            Element element = startTag.getElement();
            document.remove(new Segment(source, element.getBegin(), element.getEnd()));
        }
        return document.toString();
    }

    public static String removeAllHTMLComments(String text) {
        StringBuffer buffer = new StringBuffer();
        int startPos = 0;
        int pos = 0;
        int pos2 = 0;
        while (true) {
            if (startPos >= text.length())
                return buffer.toString();
            pos = text.indexOf("<!--", startPos);
            if (pos == -1)
                break;
            pos2 = text.indexOf("-->", pos);
            if (pos2 == -1)
                break;
            buffer.append(text.substring(startPos, pos));
            text = text.substring(pos2 + 3);
            startPos = 0;
        }
        buffer.append(text.substring(startPos));
        return buffer.toString();
    }

    /**
     * Wrap all matching words in a text with an html tag ignoring case or not.
     *
     * @param text
     * @param toBeReplaced
     * @param htmlTag
     * @param ignoreCase
     * @return
     */
    public static String wrapWithTag(String text, String toBeReplaced, String htmlTag, boolean ignoreCase) {
        if (toBeReplaced != null) {
            Pattern pattern = ignoreCase ? Pattern.compile(toBeReplaced, Pattern.CASE_INSENSITIVE) : Pattern.compile(toBeReplaced);
            Matcher matcher = pattern.matcher(text);
            return matcher.replaceAll("<" + htmlTag + ">$0</" + htmlTag + ">");
        } else {
            return new StringBuilder(text).insert(0, "<" + htmlTag + ">").append("</").append(htmlTag).append(">").toString();
        }
    }

    /**
     * Removes the /cms parameter from a link.
     */
    public static String encodePostUrls(String body, HttpServletResponse response) {
        Source source = new Source(body);
        OutputDocument outputDocument = new OutputDocument(source);
        List<Element> elements = source.getAllElements("a");

        for (Element element : elements) {
            StartTag startTag = element.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute hrefAttribute = attributes.get("href");

            if (hrefAttribute != null) {

                StringBuilder builder = new StringBuilder();
                builder.append("<a");

                for (Attribute attribute : attributes) {
                    builder.append(" ");
                    if (attribute.getName().equals("href") ||
                            attribute.getName().equals("title")) {
                        String url = attribute.toString().replace("/cms", "");
                        url = response.encodeURL(url);
                        builder.append(url);
                    } else {
                        builder.append(attribute);
                    }
                }

                builder.append(">");

                outputDocument.replace(startTag, builder);
            }
        }

        return outputDocument.toString();
    }
}
