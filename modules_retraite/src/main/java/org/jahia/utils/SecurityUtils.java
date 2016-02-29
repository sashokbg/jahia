package org.jahia.utils;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityUtils {

	private static Log log = LogFactory.getLog(SecurityUtils.class);
	
	private static Pattern[] patterns = new Pattern[]{
        // morceaux de script
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // les scripts solo
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

	
	/*
	 * encode dans une donnée utilisateur les caractères spéciaux
	 * pouvant permettre du xss
	 */
	public static String stripUserGeneratedString(String userString){
		log.info("entering script filtering for: "+userString);
		if (userString!=null && !"".equals(userString)){
            // Avoid null characters
			userString = userString.replaceAll("\0", "");
 
            // Remove all sections that match a pattern
			// TODO: remove or disrupt?
            for (Pattern scriptPattern : patterns){
            	if (scriptPattern.matcher(userString).matches()){
            		log.warn("one security pattern matched : "+scriptPattern.toString()+"for data: "+userString);
            	}
            	userString = scriptPattern.matcher(userString).replaceAll("");
            }

		}
		
		return userString;
		
	}
	
}
