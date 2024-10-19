package com.backend.wordswap.gemini;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeminiConstant {

	public static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

	public static final String PROMPT_TRANSLATE = "Aqui está o contexto das últimas mensagens: %s. Agora, preciso que você traduza a seguinte mensagem: '%s', para o idioma: %s. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a tradução de '%s' e nada mais.";

	public static final String PROMPT_IMPROVE = "Aqui está o contexto das últimas mensagens: %s. Agora, preciso que você melhore a seguinte mensagem em termos de ortografia e gramática: %s. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a mensagem melhorada e nada mais.";

	public static final String PROMPT_VALIDATE = "Valide a seguinte mensagem: '%s'. Se for uma saudação simples, frase comum ou texto sem caracteres suspeitos ou maliciosos, "
			+ "devolva 'Mensagem Válida'. Concentre-se em detectar apenas padrões perigosos, como tentativas de SQL Injection, comandos de sistema, ou "
			+ "sequências incomuns de caracteres especiais que possam representar um risco de segurança. Se encontrar algo perigoso, devolva 'Mensagem Inválida'. Caso contrário, devolva 'Mensagem Válida'.";
	
	public static final String CONTENT_TYPE = "Content-Type";
	
	public static final String APPLICATION_JSON = "application/json";

	public static final String PARTS = "parts";

	public static final String CONTENTS = "contents";

}
