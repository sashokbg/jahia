package org.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;

public class Formatter {

	public static final Log LOG = LogFactory.getLog(Formatter.class);

	public static final long GB = 1073741824L;
	public static final long MB = 1048576L;
	public static final long KB = 1024L;
	public static final char[] FILENAME_CHARS_TO_ESCAPE = new char[] { '/', '\\', ':', '\"', '|', '?', '*', '>', '<' };

	public static String getFormattedSize(long size) {
		String displaySize;
		if (size / 1073741824L > 0L) {
			displaySize = String.valueOf(size / GB) + " GO";
		} else {
			if (size / 1048576L > 0L) {
				displaySize = String.valueOf(size / MB) + " MO";
			} else {
				if (size / 1024L > 0L)
					displaySize = String.valueOf(size / KB) + " KO";
				else
					displaySize = String.valueOf(size) + " octets";
			}
		}
		return displaySize;
	}

	public static String normalizeText(String textToNormalize) {
		return normalizeText(textToNormalize, null);
	}

	/**
	 * Fonction qui permet de generer des urls normalises (enlever les espaces,
	 * apostrophes,...)
	 * 
	 * @param textToNormalize
	 *            l'url à enlever
	 * @param replaceChars
	 *            caracteres en sus a enlever (les accents par exemples...)
	 * @return
	 */
	public static String normalizeText(String textToNormalize, char[] replaceChars) {
		if (Validator.isNull(textToNormalize)) {
			return textToNormalize;
		}

		textToNormalize = GetterUtil.getString(textToNormalize);
		textToNormalize = textToNormalize.toLowerCase();
		textToNormalize = enleverAccents(textToNormalize);

		char[] charArray = textToNormalize.toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			char oldChar = charArray[i];

			char newChar = oldChar;

			if (ArrayUtils.contains(_REPLACE_CHARS, oldChar) || ((replaceChars != null) && ArrayUtils.contains(replaceChars, oldChar))) {

				newChar = CharPool.DASH;
			}

			if (oldChar != newChar) {
				charArray[i] = newChar;
			}
		}

		textToNormalize = new String(charArray);

		while (textToNormalize.contains(StringPool.DASH + StringPool.DASH)) {
			textToNormalize = StringUtils.replace(textToNormalize, StringPool.DASH + StringPool.DASH, StringPool.DASH);
		}

		if (textToNormalize.startsWith(StringPool.DASH)) {
			textToNormalize = textToNormalize.substring(1, textToNormalize.length());
		}

		if (textToNormalize.endsWith(StringPool.DASH)) {
			textToNormalize = textToNormalize.substring(0, textToNormalize.length() - 1);
		}

		return textToNormalize;
	}

	private static final char[] _REPLACE_CHARS = new char[] { ' ', ',', '\\', '\'', '\"', '(', ')', '{', '}', '?', '#', '@', '+', '~', ';',
			'$', '%' };

	public static String enleverAccents(String string) {
		StringBuilder stringBuilder = new StringBuilder(string);
		for (int i = 0; i < string.length(); i++) {
			Character character = equivalent.get(string.charAt(i));
			if (character != null)
				stringBuilder.setCharAt(i, character);
		}
		return stringBuilder.toString();
	}

	private static Map<Character, Character> equivalent;

	static {
		equivalent = new HashMap<Character, Character>();
		/* Majuscules */
		// lettre I
		equivalent.put(Character.toChars(206)[0], Character.toChars(73)[0]); // Icirc
		/* Minuscules */
		// lettre a
		equivalent.put(Character.toChars(224)[0], Character.toChars(97)[0]);// agrave
		equivalent.put(Character.toChars(225)[0], Character.toChars(97)[0]);// aacute
		equivalent.put(Character.toChars(226)[0], Character.toChars(97)[0]);// acirc
		equivalent.put(Character.toChars(227)[0], Character.toChars(97)[0]);// atilde
		equivalent.put(Character.toChars(228)[0], Character.toChars(97)[0]);// auml
		equivalent.put(Character.toChars(229)[0], Character.toChars(97)[0]);// aring
		// lettre c
		equivalent.put(Character.toChars(231)[0], Character.toChars(99)[0]);// ccedil
		// lettre e
		equivalent.put(Character.toChars(232)[0], Character.toChars(101)[0]);//
		equivalent.put(Character.toChars(233)[0], Character.toChars(101)[0]);//
		equivalent.put(Character.toChars(234)[0], Character.toChars(101)[0]);//
		equivalent.put(Character.toChars(235)[0], Character.toChars(101)[0]);//
		// lettre i
		equivalent.put(Character.toChars(236)[0], Character.toChars(105)[0]);//
		equivalent.put(Character.toChars(237)[0], Character.toChars(105)[0]);//
		equivalent.put(Character.toChars(238)[0], Character.toChars(105)[0]);//
		equivalent.put(Character.toChars(239)[0], Character.toChars(105)[0]);//
		// lettre o
		equivalent.put(Character.toChars(240)[0], Character.toChars(111)[0]);//
		equivalent.put(Character.toChars(242)[0], Character.toChars(111)[0]);//
		equivalent.put(Character.toChars(243)[0], Character.toChars(111)[0]);//
		equivalent.put(Character.toChars(244)[0], Character.toChars(111)[0]);//
		equivalent.put(Character.toChars(245)[0], Character.toChars(111)[0]);//
		equivalent.put(Character.toChars(246)[0], Character.toChars(111)[0]);//
		// lettre u
		equivalent.put(Character.toChars(249)[0], Character.toChars(117)[0]);//
		equivalent.put(Character.toChars(250)[0], Character.toChars(117)[0]);//
		equivalent.put(Character.toChars(251)[0], Character.toChars(117)[0]);//
		equivalent.put(Character.toChars(252)[0], Character.toChars(117)[0]);//
		// lettre y
		equivalent.put(Character.toChars(253)[0], Character.toChars(121)[0]);//
		equivalent.put(Character.toChars(255)[0], Character.toChars(121)[0]);//
	}

	private static final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);

	/**
	 * Permet de reconnaitre les liens dans du texte brut
	 * 
	 * par exemple le texte suivant :
	 * 
	 * je suis une tarlouse de bonne famille pour plus d'information
	 * www.google.fr mais je sais que c'est une request http:au besoin aller sur
	 * le serveur habituel (ftp://ftp.ag2r.fr) ou suivre https://lesfousduweb.ma
	 * 
	 * devient :
	 * 
	 * je suis une tarlouse de bonne famille pour plus d'information <a
	 * href="www.google.fr">www.google.fr</a> mais je sais que c'est une request
	 * http:au besoin aller sur le serveur habituel (<a
	 * href="ftp://ftp.ag2r.fr">ftp://ftp.ag2r.fr</a>) ou suivre <a
	 * href="https://lesfousduweb.ma">https://lesfousduweb.ma</a>
	 * 
	 * @param initialtext
	 *            : le texte brut (sans balise)
	 * @return formatedtext : le texte avec les balises de lien format html
	 */
	public static String convertLinksToHTML(String initialtext, String cssClassName, boolean forceExternal, boolean excludeSchemeFromLabel,
			int maxTruncate) {
		StringBuilder formatedtext = new StringBuilder();
		String target = forceExternal ? "_blank" : "_self";
		String className = StringUtils.isEmpty(cssClassName) ? "mbLink" : cssClassName;
		// Formattage des liens du front
		StringTokenizer tokenizer = new StringTokenizer(initialtext, " \n\r\t(){}[]");
		String token = null;

		String tmpValue = new String(initialtext);
		int indexOf = 0;

		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			String urlTest = token;
			String label = null;

			label = excludeSchemeFromLabel ? token.replaceAll("http://|ftp://", "") : token;
			urlTest = enleverAccents(token);

			if (maxTruncate > 0 && label.length() > maxTruncate)
				label = label.substring(0, maxTruncate) + "...";

			indexOf = tmpValue.indexOf(token);
			formatedtext.append(tmpValue.substring(0, indexOf));
			tmpValue = new String(tmpValue.substring(indexOf + token.length(), tmpValue.length()));

			if (urlValidator.isValid(urlTest)) {
				formatedtext.append("<a class=\"" + className + "\" target=\"" + target + "\" title=\"" + token + "\" href=\""
						+ StringEscapeUtils.unescapeXml(token) + "\">" + label + "</a>");
			} else if (token.startsWith("www.") && urlValidator.isValid("http://" + urlTest)) {
				formatedtext.append("<a class=\"" + className + "\" target=\"" + target + "\" title=\"" + token + "\" href=\"http://"
						+ StringEscapeUtils.unescapeXml(token) + "\">" + label + "</a>");
			} else if (token.startsWith("ftp.") && urlValidator.isValid("ftp://" + urlTest)) {
				formatedtext.append("<a class=\"" + className + "\" target=\"" + target + "\" title=\"" + token + "\" href=\"ftp://"
						+ StringEscapeUtils.unescapeXml(token) + "\">" + label + "</a>");
			} else
				formatedtext.append(token);
		}

		// Formattage des liens du back :
		return formatedtext.toString();
	}

	public static final String SHORT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String SIMPLE_DATE_FORMAT = "dd MMMM yyyy";
	public static final String FULL_DATE_FORMAT = "dd/MM/yyyy' - 'HH'H'mm";

	/**
	 * Retourne la date en format specifie. Peut remplacer la partie
	 * jour/mois/annee par l'adverbe adequat.
	 * 
	 * @param date
	 *            la date a formater
	 * @param format
	 *            le pattern a prendre en compte
	 * @param replaceWithHeuristicAdverbial
	 *            remplace par l'adverbe adequat si vrai
	 * @return la chaine formatee de la date
	 */
	public static String formatDate(Date date, String format, Boolean replaceWithHeuristicAdverbial) {
		try {
			Date nowDate = new Date();

			Calendar post = new GregorianCalendar();
			post.setTime(date);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Initialized post date : " + new SimpleDateFormat().format(post.getTime()));
			}

			Calendar yesterday = new GregorianCalendar();
			yesterday.setTime(nowDate);
			yesterday.setLenient(true);
			yesterday.add(Calendar.DAY_OF_MONTH, -1);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Initialized yesterday date : " + (new SimpleDateFormat().format(yesterday.getTime())));
			}

			if (replaceWithHeuristicAdverbial && (yesterday.before(post) || yesterday.equals(post))) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Post date is earlier than yesterday 00:00:00 - 1s");
				}
				String patternHour = format.replaceFirst("[dD]+[/-\\\\.\\s]?[M]+[/-\\\\.\\s]?[yY]+", "");
				Calendar now = new GregorianCalendar();
				now.setTime(nowDate);
				now.set(Calendar.HOUR_OF_DAY, 0);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Initialized now date : " + (new SimpleDateFormat().format(now.getTime())));
				}

				if (now.before(post) || now.equals(post)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Post date is earlier than today 00:00:00 - 1s");
					}
					return "aujourd'hui" + (StringUtils.isNotEmpty(patternHour) ? new SimpleDateFormat(patternHour).format(date) : "");
				}

				return "hier" + (StringUtils.isNotEmpty(patternHour) ? new SimpleDateFormat(patternHour).format(date) : "");
			}

			return new SimpleDateFormat(format).format(date);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
			return formatDate(date);
		}
	}

	/**
	 * Formatter de type jour mois annnee ("dd MMMM yyyy")
	 */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

	/**
	 * Retourne la date en format "dd MMMM yyyy" sans les heures et minutes
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * Formatter de type jour mois annnee ("dd/MM/yyyy' - 'HH'H'mm")
	 */
	private static SimpleDateFormat dateFormatWithHours = new SimpleDateFormat(FULL_DATE_FORMAT);

	/**
	 * Retourne la date en format "dd/MM/yyyy' - 'HH'H'mm" sans les heures et
	 * minutes
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateWithHours(Date date) {
		return dateFormatWithHours.format(date);
	}

	public static enum _Character {

		APOS(Character.toString(Character.toChars(39)[0])), //

		ICIRC(Character.toString(Character.toChars(206)[0])), //

		AGRAVE(Character.toString(Character.toChars(224)[0])), //
		ACIRC(Character.toString(Character.toChars(226)[0])), //

		CCDILLE(Character.toString(Character.toChars(231)[0])), //
		
		EGRAVE(Character.toString(Character.toChars(232)[0])), //
		EACUTE(Character.toString(Character.toChars(233)[0])), //
		ECIRC(Character.toString(Character.toChars(234)[0])), //

		OCIRC(Character.toString(Character.toChars(244)[0]));

		private String _char;

		_Character(String _char) {
			this._char = _char;
		}

		public String get() {
			return this._char;
		}

		@Override
		public String toString() {
			return this._char;
		}

	}
}
