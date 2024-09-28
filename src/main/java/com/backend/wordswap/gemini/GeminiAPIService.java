package com.backend.wordswap.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.wordswap.chat.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class GeminiAPIService {

	@Autowired
	private RestTemplate restTemplate;

	@Retry(name = "geminiService", fallbackMethod = "fallbackTranslate")
	public String translateText(String text, String language) throws Exception {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPrompt(text, GeminiConstant.PROMPT_TRANSLATE));
		contentNode.set("parts", objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set("contents", objectMapper.createArrayNode().add(contentNode));

		String requestBody = objectMapper.writeValueAsString(requestBodyNode);

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return this.extractTextFromResponse(response.getBody());
	}

	public String extractTextFromResponse(String jsonResponse) throws Exception {
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

	public String improveText(String text) throws Exception {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPrompt(text, GeminiConstant.PROMPT_IMPROVE));
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

		return this.extractTextFromResponse(response.getBody());
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

		return this.extractTextFromResponse(response.getBody());
	}

}
