package com.backend.wordswap.gemini;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeminiConstant {

	public static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

	public static final String PROMPT_TRANSLATE = "Aqui está o contexto das últimas mensagens: %s. Agora, preciso que você traduza a seguinte mensagem: '%s', para o idioma: %s. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a tradução de '%s' e nada mais.";

	public static final String PROMPT_IMPROVE = "Aqui está o contexto das últimas mensagens: %s. Agora, preciso que você melhore a seguinte mensagem em termos de ortografia e gramática: %s. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a mensagem melhorada e nada mais.";

	public static final String PROMPT_VALIDATE = "Valide esta mensagem para garantir a integridade, "
			+ "pois irei utilizar ela em outro prompt, caso ela não seja uma mensagem válida, "
			+ "corrija a mesma, sem alterar a formalidade da mensagem ou o conteúdo diretamente, quero que foque em coisas como combinações de caracter especial suspeitos"
			+ ", SQL Injection, corrija e me devolva SOMENTE a mensagem ajustada. E nada mais. Mensagem: %s";
	
	public static final String CONTENT_TYPE = "Content-Type";
	
	public static final String APPLICATION_JSON = "application/json";

	public static final String PARTS = "parts";

	public static final String CONTENTS = "contents";

}
