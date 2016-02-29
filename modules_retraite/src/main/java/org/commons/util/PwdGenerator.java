package org.commons.util;

import org.commons.util.Validator;

/**
 * @author el-aarko
 */
public class PwdGenerator {

	public static String KEY1 = "0123456789";

	public static String KEY2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String KEY3 = "abcdefghijklmnopqrstuvwxyz";

	public static String getPinNumber() {
		return _getPassword(KEY1, 4, true);
	}

	public static String getPassword() {
		return getPassword(8);
	}

	public static String getPassword(int length) {
		return _getPassword(KEY1 + KEY2 + KEY3, length, true);
	}

	public static String getPassword(String key, int length) {
		return getPassword(key, length, true);
	}

	public static String getPassword(
		String key, int length, boolean useAllKeys) {

		return _getPassword(key, length, useAllKeys);
	}

	private static String _getPassword(
		String key, int length, boolean useAllKeys) {

		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(key.charAt((int)(Math.random() * key.length())));
		}

		String password = sb.toString();

		if (!useAllKeys) {
			return password;
		}

		boolean invalidPassword = false;

		if (key.contains(KEY1)) {
			if (Validator.isNull(extractDigits(password))) {
				invalidPassword = true;
			}
		}

		if (key.contains(KEY2)) {
			if (password.equals(password.toLowerCase())) {
				invalidPassword = true;
			}
		}

		if (key.contains(KEY3)) {
			if (password.equals(password.toUpperCase())) {
				invalidPassword = true;
			}
		}

		if (invalidPassword) {
			return _getPassword(key, length, useAllKeys);
		}

		return password;
	}
	
	public static String extractDigits(String s) {
 		if (s == null) {
 			return "";
 		}
 
 		StringBuilder sb = new StringBuilder();
 
 		char[] chars = s.toCharArray();
 
 		for (int i = 0; i < chars.length; i++) {
 			if (Validator.isDigit(chars[i])) {
 				sb.append(chars[i]);
 			}
 		}
 
 		return sb.toString();
 	}

}