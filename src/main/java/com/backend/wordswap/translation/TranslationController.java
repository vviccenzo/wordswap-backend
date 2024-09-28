package com.backend.wordswap.translation;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.translation.dto.TranslationDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/translation")
public class TranslationController {

	private final TranslationService translationAPIService;

	@GetMapping(path = "/find-options-translation", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TranslationDTO> findOptionsTranslation() {
		return this.translationAPIService.findOptionsTranslation();
	}

}
