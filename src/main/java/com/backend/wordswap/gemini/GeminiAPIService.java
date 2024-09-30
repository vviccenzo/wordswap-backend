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

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class GeminiAPIService {

	@Autowired
	private RestTemplate restTemplate;

	@Retry(name = "geminiService", fallbackMethod = "fallbackTranslate")
	public String translateText(String text, String language, String context) throws Exception {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPromptTranslate(text, language, context));
		contentNode.set("parts", objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set("contents", objectMapper.createArrayNode().add(contentNode));

		String requestBody = objectMapper.writeValueAsString(requestBodyNode);

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

	public String improveText(String text, String context) throws Exception {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPromptImprove(context, text));
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

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

	public String validateContent(String content) throws Exception {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPrompt(content, GeminiConstant.PROMPT_VALIDATE));
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

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

}
