package com.backend.wordswap.translation.configuration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TranslationConfigResponseDTO {

	private String receivingTranslation;

	private Boolean isReceivingTranslation = Boolean.FALSE;

	private Boolean isImprovingText = Boolean.FALSE;

}
