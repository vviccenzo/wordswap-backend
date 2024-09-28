package com.backend.wordswap.translation.configuration;

import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.ConversationRepository;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigDTO;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TranslationConfigurationService {

	private final UserRepository userRepository;

	private final ConversationRepository convRepository;

	private final TranslationConfigurationRepository translationConfigurationRepository;

	public TranslationConfigurationService(TranslationConfigurationRepository translationConfigRepository, UserRepository userRepository, ConversationRepository convRepository) {
		this.translationConfigurationRepository = translationConfigRepository;
		this.userRepository = userRepository;
		this.convRepository = convRepository;
	}

	public TranslationConfigResponseDTO configurateTranslation(TranslationConfigDTO dto) {
		UserModel user = this.findUserById(dto.getUserId());
		ConversationModel conversation = this.findConversationById(dto.getConversationId());

		this.clearPreviousConfigurations(dto.getUserId(), dto.getConversationId());
		this.saveTranslationConfiguration(conversation, user, TranslationType.RECEIVING, dto.getReceivingTranslation(), dto.getIsReceivingTranslation());
		this.saveTranslationConfiguration(conversation, user, TranslationType.IMPROVING, null, dto.getIsImprovingText());

		return TranslationConfigFactory.buildTranslationConfigResponse(dto);
	}

	private UserModel findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o id: " + userId));
	}

	private ConversationModel findConversationById(Long conversationId) {
		return this.convRepository.findById(conversationId).orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada com o id: " + conversationId));
	}

	private void clearPreviousConfigurations(Long userId, Long conversationId) {
		this.translationConfigurationRepository.deleteAllByUserIdAndConversationId(userId, conversationId);
	}

	private void saveTranslationConfiguration(ConversationModel conversation, UserModel user, TranslationType type, String targetLanguage, Boolean isTranslationEnabled) {
		if (isTranslationEnabled != null && isTranslationEnabled) {
			TranslationConfigurationModel config = TranslationConfigFactory.createTranslationConfig(conversation, user, type, targetLanguage, isTranslationEnabled);
			this.translationConfigurationRepository.save(config);
		}
	}

}
