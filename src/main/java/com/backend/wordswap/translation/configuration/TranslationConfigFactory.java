package com.backend.wordswap.translation.configuration;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.entity.UserModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslationConfigFactory {

	public static TranslationConfigurationModel createTranslationConfig(ConversationModel conversation, UserModel user, TranslationType type, String targetLanguage, Boolean isActive) {
		TranslationConfigurationModel config = new TranslationConfigurationModel();
		if(isActive) {
			config.setConversation(conversation);
			config.setUser(user);
			config.setType(type);
			config.setTargetLanguage(targetLanguage);
			config.setIsActive(isActive);

			return config;
		}

		return null;
	}

	public static TranslationConfigResponseDTO buildTranslationConfigResponse(TranslationConfigDTO dto) {
		TranslationConfigResponseDTO response = new TranslationConfigResponseDTO();
		response.setIsReceivingTranslation(dto.getIsReceivingTranslation());
		response.setReceivingTranslation(dto.getReceivingTranslation());
		response.setIsImprovingText(dto.getIsImprovingText());

		return response;
	}

}
