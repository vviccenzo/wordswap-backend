package com.backend.wordswap.translation.configuration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translation-configuration")
public class TranslationConfigurationController {

	private final TranslationConfigurationService translationConfigurationService;

	public TranslationConfigurationController(TranslationConfigurationService translationConfigurationService) {
		this.translationConfigurationService = translationConfigurationService;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public void configurateTranslation(@RequestBody TranslationConfigDTO dto) {
		this.translationConfigurationService.configurateTranslation(dto);
	}
}
