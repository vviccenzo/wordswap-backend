package com.backend.wordswap.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.wordswap.gemini.exception.GeminiJsonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class GeminiAPIService {

	private RestTemplate restTemplate;

	GeminiAPIService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Value("${gemini.key}")
	public String geminiKey;

	@Retry(name = "geminiService", fallbackMethod = "fallbackTranslate")
	public String translateText(String text, String language, String context) throws JsonProcessingException {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, geminiKey);

		HttpHeaders headers = new HttpHeaders();
		headers.set(GeminiConstant.CONTENT_TYPE, GeminiConstant.APPLICATION_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPromptTranslate(text, language, context));
		contentNode.set(GeminiConstant.PARTS, objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set(GeminiConstant.CONTENTS, objectMapper.createArrayNode().add(contentNode));

		String requestBody = objectMapper.writeValueAsString(requestBodyNode);

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

	public String improveText(String text, String context) throws JsonProcessingException {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, geminiKey);

		HttpHeaders headers = new HttpHeaders();
		headers.set(GeminiConstant.CONTENT_TYPE, GeminiConstant.APPLICATION_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPromptImprove(context, text));
		contentNode.set(GeminiConstant.PARTS, objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set(GeminiConstant.CONTENTS, objectMapper.createArrayNode().add(contentNode));

		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(requestBodyNode);
		} catch (Exception e) {
			throw new GeminiJsonException(e.getMessage());
		}

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

	public String validateContent(String content) throws JsonProcessingException {
		String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, geminiKey);

		HttpHeaders headers = new HttpHeaders();
		headers.set(GeminiConstant.CONTENT_TYPE, GeminiConstant.APPLICATION_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode contentNode = objectMapper.createObjectNode();
		ObjectNode partsNode = objectMapper.createObjectNode();

		partsNode.put("text", GeminiUtils.formatPrompt(content, GeminiConstant.PROMPT_VALIDATE));
		contentNode.set(GeminiConstant.PARTS, objectMapper.createArrayNode().add(partsNode));

		ObjectNode requestBodyNode = objectMapper.createObjectNode();
		requestBodyNode.set(GeminiConstant.CONTENTS, objectMapper.createArrayNode().add(contentNode));

		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(requestBodyNode);
		} catch (Exception e) {
			throw new GeminiJsonException(e.getMessage());
		}

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

		return GeminiUtils.extractTextFromResponse(response.getBody());
	}

}
