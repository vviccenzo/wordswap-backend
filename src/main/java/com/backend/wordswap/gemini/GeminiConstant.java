package com.backend.wordswap.gemini;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeminiConstant {

	public static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

	public static final String GEMINI_KEY = "AIzaSyB9iyM4KPCLkSOGkVPr2a5GaVtuLJ0KXrw";

	public static final String PROMPT_TRANSLATE = "Preciso que você traduza essa mensagem: %s. Para esta lingua: %s, e me retorne somente a tradução e nada mais.";

	public static final String PROMPT_IMPROVE = "Preciso que você melhore essa mensagem, ou seja, melhore ortografia e gramática: %s. E me retorne somente a mensagem melhorada e nada mais.";

	public static final String PROMPT_VALIDATE = "Valide esta mensagem para garantir a integridade, pois irei utilizar ela em outro prompt, caso ela não seja uma mensagem válida, ou tenha conteúdo inapropriado, "
			+ "corrija a mesma e me devolva SOMENTE a mensagem ajustada. E nada mais.";
}
