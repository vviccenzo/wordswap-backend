package com.backend.wordswap.translate;

import org.junit.jupiter.api.Test;

import com.backend.wordswap.translation.dto.TranslationDTO;

import static org.assertj.core.api.Assertions.assertThat;

class TranslationDTOTest {

	@Test
	void testTranslationDTO() {
		TranslationDTO translationDTO = new TranslationDTO("English", "en");

		assertThat(translationDTO.name()).isEqualTo("English");
		assertThat(translationDTO.code()).isEqualTo("en");
	}

	@Test
	void testTranslationDTO_Equality() {
		TranslationDTO dto1 = new TranslationDTO("English", "en");
		TranslationDTO dto2 = new TranslationDTO("English", "en");

		assertThat(dto1).isEqualTo(dto2);

		TranslationDTO dto3 = new TranslationDTO("Spanish", "es");
		assertThat(dto1).isNotEqualTo(dto3);
	}

	@Test
	void testTranslationDTO_HashCode() {
		TranslationDTO dto1 = new TranslationDTO("English", "en");
		TranslationDTO dto2 = new TranslationDTO("English", "en");

		assertThat(dto1.hashCode()).hasSameHashCodeAs(dto2.hashCode());

		TranslationDTO dto3 = new TranslationDTO("Spanish", "es");
		assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
	}

	@Test
	void testTranslationDTO_ToString() {
		TranslationDTO translationDTO = new TranslationDTO("English", "en");
		assertThat(translationDTO.toString()).isEqualTo("TranslationDTO[name=English, code=en]");
	}
}
