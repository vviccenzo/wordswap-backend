package com.backend.wordswap.translation.configuration;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.ConversationRepository;
import com.backend.wordswap.conversation.entity.ConversationModel;
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

	public TranslationConfigurationService(TranslationConfigurationRepository translationConfigRepository,
			UserRepository userRepository, ConversationRepository convRepository) {
		this.translationConfigurationRepository = translationConfigRepository;
		this.userRepository = userRepository;
		this.convRepository = convRepository;
	}

	public void configurateTranslation(TranslationConfigDTO dto) {
		Optional<UserModel> optUser = this.userRepository.findById(dto.getUserId());
		if (optUser.isEmpty()) {
			throw new UserNotFoundException("Usuário não encontrado com o id: " + dto.getUserId());
		}

		Optional<ConversationModel> optConv = this.convRepository.findById(dto.getConversartionId());
		if (optConv.isEmpty()) {
			throw new EntityNotFoundException("Conversa não encontrada com o id: " + dto.getConversartionId());
		}

		TranslationConfigurationModel configReceiving = this.translationConfigurationRepository
				.findById(dto.getReceiverId()).orElse(new TranslationConfigurationModel());

		configReceiving.setConversation(optConv.get());
		configReceiving.setUser(optUser.get());
		configReceiving.setType(TranslationType.RECEIVING);
		configReceiving.setTargetLanguage(dto.getReceivingTranslation());
		configReceiving.setIsActive(dto.getIsReceivingTranslation());

		this.translationConfigurationRepository.save(configReceiving);

		TranslationConfigurationModel configSending = this.translationConfigurationRepository
				.findById(dto.getSendingId()).orElse(new TranslationConfigurationModel());

		configSending.setConversation(optConv.get());
		configSending.setUser(optUser.get());
		configSending.setType(TranslationType.SENDING);
		configSending.setTargetLanguage(dto.getSendingTranslation());
		configReceiving.setIsActive(dto.getIsSendingTranslation());

		this.translationConfigurationRepository.save(configSending);
	}
}
