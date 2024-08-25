package com.backend.wordswap.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GeminiAPIService {

	@Autowired
	private RestTemplate restTemplate;

	private static final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s";

	private static final String GEMINI_KEY = "AIzaSyDeFBuYaYIxP4DMFUOeQg4Uy7YfzUG8y9g";

	private static final String PROMPT = "Preciso que você traduza essa mensagem: %s. Para esta lingua: %s, e me retorne somente a tradução e nada mais.";

	public String translateText(String text, String language) {
		String apiUrl = String.format(API_URL_TEMPLATE, GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", this.formatPrompt(text, language));
		contentNode.set("parts", objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set("contents", objectMapper.createArrayNode().add(contentNode));

		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(requestBodyNode);
		} catch (Exception e) {
			throw new RuntimeException("Failed to construct JSON request body", e);
		}

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return response.getBody();
	}

	private String formatPrompt(String text, String language) {
		return String.format(PROMPT, text, language);
	}
}
