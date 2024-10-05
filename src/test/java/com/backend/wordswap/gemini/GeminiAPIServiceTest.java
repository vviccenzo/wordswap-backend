package com.backend.wordswap.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GeminiAPIServiceTest {

	@InjectMocks
	private GeminiAPIService geminiAPIService;

	@Mock
	private RestTemplate restTemplate;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testTranslateText() throws Exception {
	    String text = "Hello, world!";
	    String language = "es";
	    String context = "greeting";
	    String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);
	    
	    String expectedResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"¡Hola, mundo!\"}]}}]}";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");

	    ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
	    when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
	        .thenReturn(mockResponse);

	    String actualResponse = this.geminiAPIService.translateText(text, language, context);

	    assertEquals("¡Hola, mundo!", actualResponse);
	}

	@Test
	void testImproveText() throws Exception {
	    String text = "This is a test.";
	    String context = "improving";
	    String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);
	    
	    String expectedResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"This is a better test.\"}]}}]}";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");

	    ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
	    when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
	        .thenReturn(mockResponse);

	    String actualResponse = geminiAPIService.improveText(text, context);

	    assertEquals("This is a better test.", actualResponse);
	}

	@Test
	void testValidateContent() throws Exception {
	    String content = "Sample content to validate.";
	    String apiUrl = String.format(GeminiConstant.API_URL_TEMPLATE, GeminiConstant.GEMINI_KEY);

	    String expectedResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Content is valid.\"}]}}]}";

	    ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
	    when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
	            .thenReturn(mockResponse);

	    String actualResponse = geminiAPIService.validateContent(content);

	    assertEquals("Content is valid.", actualResponse);
	}

	@Test
	void testTranslateTextFallback() throws Exception {
		String text = "Hello, world!";
		String language = "es";
		String context = "greeting";

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
				.thenThrow(new RuntimeException("Service unavailable"));

		Exception exception = assertThrows(Exception.class, () -> {
			geminiAPIService.translateText(text, language, context);
		});

		assertEquals("Service unavailable", exception.getMessage());
	}

}
