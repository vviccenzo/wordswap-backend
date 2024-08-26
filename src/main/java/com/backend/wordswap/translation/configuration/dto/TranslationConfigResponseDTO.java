package com.backend.wordswap.translation.configuration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TranslationConfigResponseDTO {

	private String sendingTranslation;

	private String receivingTranslation;

	private Boolean isSendingTranslation = Boolean.FALSE;

	private Boolean isReceivingTranslation = Boolean.FALSE;

}
