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
	    String text = "Hello!";
	    String language = "es";
	    String result = GeminiUtils.formatPromptTranslate(text, language);
	    String expected = String.format(GeminiConstant.PROMPT_TRANSLATE, text, language, text);
	    assertEquals(expected, result);
	}

	@Test
	void testFormatPromptImprove() {
	    String text = "Hello!";
	    String result = GeminiUtils.formatPromptImprove(text);
	    String expected = String.format(GeminiConstant.PROMPT_IMPROVE, text);
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
