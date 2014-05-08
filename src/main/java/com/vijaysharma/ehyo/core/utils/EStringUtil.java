package com.vijaysharma.ehyo.core.utils;

public class EStringUtil {
	public static String makeFirstLetterUpperCase(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
