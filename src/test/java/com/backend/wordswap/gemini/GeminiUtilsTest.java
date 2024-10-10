package com.backend.wordswap.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GeminiUtilsTest {

	@Test
	void testFormatPrompt() {
		String text = "Hello, World!";
		String constant = "This is a message: %s";
		String result = GeminiUtils.formatPrompt(text, constant);
		assertEquals("This is a message: Hello, World!", result);
	}

	@Test
	void testFormatPromptTranslate() {
	    String context = "context";
	    String text = "Hello!";
	    String language = "es";
	    String result = GeminiUtils.formatPromptTranslate(context, text, language);
	    String expected = "Aqui está o contexto das últimas mensagens: context. Agora, preciso que você traduza a seguinte mensagem: 'Hello!', para o idioma: es. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a tradução de 'Hello!' e nada mais.";
	    assertEquals(expected, result);
	}

	@Test
	void testFormatPromptImprove() {
	    String context = "context";
	    String text = "Hello!";
	    String result = GeminiUtils.formatPromptImprove(context, text);
	    String expected = "Aqui está o contexto das últimas mensagens: context. Agora, preciso que você melhore a seguinte mensagem em termos de ortografia e gramática: Hello!. Baseie-se no contexto e escolha o tom apropriado (formal ou informal). Retorne apenas a mensagem melhorada e nada mais.";
	    assertEquals(expected, result);
	}

	@Test
	void testExtractTextFromResponse() throws Exception {
		String jsonResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Translation result\"}]}}]}";
		String expectedText = "Translation result";

		String result = GeminiUtils.extractTextFromResponse(jsonResponse);
		assertEquals(expectedText, result);
	}

	@Test
	void testExtractTextFromResponseNoCandidates() throws Exception {
		String jsonResponse = "{\"candidates\":[]}";

		String result = GeminiUtils.extractTextFromResponse(jsonResponse);
		assertNull(result);
	}

	@Test
	void testExtractTextFromResponseInvalidJson() {
		String jsonResponse = "Invalid JSON";

		Exception exception = assertThrows(Exception.class, () -> {
			GeminiUtils.extractTextFromResponse(jsonResponse);
		});

		assertNotNull(exception);
	}
}
