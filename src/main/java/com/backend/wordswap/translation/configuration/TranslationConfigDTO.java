package com.backend.wordswap.translation.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationConfigDTO {

	private Long userId;

	private Long sendingId;

	private Long receiverId;

	private Long conversationId;

	private String receivingTranslation;

	private String sendingTranslation;

	private Boolean isSendingTranslation;

	private Boolean isReceivingTranslation;

}
