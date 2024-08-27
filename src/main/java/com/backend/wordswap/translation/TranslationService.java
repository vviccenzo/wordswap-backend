package com.backend.wordswap.translation;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.wordswap.translation.dto.TranslationDTO;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.LanguageListOption;
import com.google.cloud.translate.TranslateOptions;

@Service
public class TranslationService {

	@Value("${api.cloud.translate.key}")
	private static String API_CLOUD_TRANSLATE_KEY;

	@SuppressWarnings("deprecation")
	public List<TranslationDTO> findOptionsTranslation() {
		Translate translate = TranslateOptions.newBuilder().setApiKey(API_CLOUD_TRANSLATE_KEY).build().getService();

		return translate.listSupportedLanguages(LanguageListOption.targetLanguage("pt-BR")).stream()
				.map(language -> new TranslationDTO(language.getName(), language.getCode())).toList();
	}

	@SuppressWarnings("deprecation")
	public String detectLanguage(String text) {
		Translate translate = TranslateOptions.newBuilder().setApiKey(API_CLOUD_TRANSLATE_KEY).build().getService();

		return translate.detect(text).getLanguage();
	}

}
