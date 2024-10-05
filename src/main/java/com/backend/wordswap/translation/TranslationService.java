package com.backend.wordswap.translation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.wordswap.translation.dto.TranslationDTO;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.LanguageListOption;
import com.google.cloud.translate.TranslateOptions;

@Service
public class TranslationService {

	private static String API_CLOUD_TRANSLATE_KEY = "AIzaSyAoV9WJI2TyzWGSSfoOuU-tGoHdRBwUE60";

	@SuppressWarnings("deprecation")
	public List<TranslationDTO> findOptionsTranslation() {
		Translate translate = TranslateOptions.newBuilder().setApiKey(API_CLOUD_TRANSLATE_KEY).build().getService();

		return translate.listSupportedLanguages(LanguageListOption.targetLanguage("pt-BR")).stream()
				.map(language -> new TranslationDTO(language.getName(), language.getCode())).toList();
	}

}
