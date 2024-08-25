package com.backend.wordswap.translation.configuration.factory;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.user.entity.UserModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslationConfigurationFactory {

	public static TranslationConfigurationModel getOrCreateTranslationConfiguration(MessageCreateDTO dto,
			UserModel sender, ConversationModel conversation) {
		if (dto.getConversationId() != null) {
			return conversation.getTranslationConfigurations().stream()
					.filter(tc -> tc.getUser().getId().equals(sender.getId())).findFirst()
					.orElse(new TranslationConfigurationModel());
		} else {
			TranslationConfigurationModel translationConfig = new TranslationConfigurationModel();
			translationConfig.setTargetLanguage(dto.getTargetLanguage());
			translationConfig.setUser(sender);
			translationConfig.setConversation(conversation);

			return translationConfig;
		}
	}
}
