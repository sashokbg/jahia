package org.commons.util;

import org.apache.commons.lang.StringUtils;


/**
 * Classe utilitaire pour le traitement de texte.
 * 
 * @author Baroin
 */

public class StringUtil {

	/**
	 * 
	 * @param str
	 * @param lenght
	 * @return
	 */
	public static String cutString(String str, Integer lenght) {
		String result = str;
		if (str != null && str.length() > lenght) {
			// Cas du body de la réponse avec citation :
			int index = str.indexOf(" \t\t");
			if (index != -1) {
				str = StringUtils.substring(str, index);
				str = StringUtils.replace(str, " \t\t", "");
				str = StringUtils.replace(str, "\\<.*?>", "");
				str = StringUtils.trim(str);
				result = str;
			}//fin du cas
			
			
			String[] splitStr = str.split(" ");
			int countCaracteres = 0;
			result = "";
			for (int j = 0; j < splitStr.length; j++) {
				countCaracteres += splitStr[j].length();
				if (countCaracteres > lenght)
					break;
				result += splitStr[j] + " ";
				countCaracteres++;
			}
			// on enleve le dernier espace
			result = StringUtils.substring(result, 0,(result.length()>=1)? result.length()-1 : 0);
			result += "...";			
		}
		return result;
	}

}