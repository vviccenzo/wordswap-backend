package com.backend.wordswap.translation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.translation.dto.TranslationDTO;

@RestController
@RequestMapping("/translation")
public class TranslationController {

	@Autowired
	private TranslationService translationAPIService;

	@GetMapping(path = "/find-options-translation", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TranslationDTO> findOptionsTranslation() {
		return this.translationAPIService.findOptionsTranslation();
	}

}
