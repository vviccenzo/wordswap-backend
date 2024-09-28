package com.backend.wordswap.gemini;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeminiUtils {

	public static String formatPrompt(String text, String constant) {
		return String.format(constant, text);
	}

	public static String fallbackTranslate(String content, String targetLanguage, Throwable t) {
		return "Tradução temporariamente indisponível";
	}

}
