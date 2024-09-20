package com.backend.wordswap.translation.configuration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;

@RestController
@RequestMapping("/translation-configuration")
public class TranslationConfigurationController {

	private final TranslationConfigurationService translationConfigurationService;

	public TranslationConfigurationController(TranslationConfigurationService translationConfigurationService) {
		this.translationConfigurationService = translationConfigurationService;
	}

	@PostMapping(path = "/configuration")
	public TranslationConfigResponseDTO configurateTranslation(@RequestBody TranslationConfigDTO dto) {
		return this.translationConfigurationService.configurateTranslation(dto);
	}

}
