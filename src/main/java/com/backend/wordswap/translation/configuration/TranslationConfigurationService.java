package com.backend.wordswap.translation.configuration;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.ConversationRepository;
import com.backend.wordswap.conversation.entity.ConversationModel;
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
		UserModel user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o id: " + dto.getUserId()));

		ConversationModel conversation = convRepository.findById(dto.getConversationId()).orElseThrow(
				() -> new EntityNotFoundException("Conversa não encontrada com o id: " + dto.getConversationId()));

		this.translationConfigurationRepository.deleteAllByUserIdAndConversationId(dto.getUserId(), dto.getConversationId());

		TranslationConfigurationModel configReceiving = TranslationConfigFactory.createTranslationConfig(conversation,
				user, TranslationType.RECEIVING, dto.getReceivingTranslation(), dto.getIsReceivingTranslation());

		TranslationConfigurationModel configImprove = TranslationConfigFactory.createTranslationConfig(conversation,
				user, TranslationType.IMPROVING, null, dto.getIsImprovingText());

		if(Objects.nonNull(configReceiving)) {
			this.translationConfigurationRepository.save(configReceiving);
		}

		if(Objects.nonNull(configImprove)) {
			this.translationConfigurationRepository.save(configImprove);
		}

		return TranslationConfigFactory.buildTranslationConfigResponse(dto);
	}
}
