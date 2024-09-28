package com.backend.wordswap.translation.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationConfigDTO {

	private Long userId;

	private Long sendingId;

	private Long receiverId;

	private Long conversationId;

	private String receivingTranslation;

	private Boolean isReceivingTranslation;

	private Boolean isImprovingText;

}
