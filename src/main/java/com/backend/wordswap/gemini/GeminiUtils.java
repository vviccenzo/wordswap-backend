package com.backend.wordswap.gemini;

import com.backend.wordswap.chat.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeminiUtils {

	public static String formatPrompt(String text, String constant) {
		return String.format(constant, text);
	}

	public static String formatPromptTranslate(String context, String text, String language) {
		return String.format(GeminiConstant.PROMPT_TRANSLATE, context, text, language, text);
	}

	public static String formatPromptImprove(String context, String text) {
		return String.format(GeminiConstant.PROMPT_IMPROVE, context, text);
	}

	public static String extractTextFromResponse(String jsonResponse) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ChatResponse chatResponse = objectMapper.readValue(jsonResponse, ChatResponse.class);

		if (chatResponse.getCandidates() != null && chatResponse.getCandidates().length > 0) {
			ChatResponse.Candidates candidate = chatResponse.getCandidates()[0];
			if (candidate.getContent() != null && candidate.getContent().getParts().length > 0) {
				return candidate.getContent().getParts()[0].getText();
			}
		}

		return null;
	}
}
