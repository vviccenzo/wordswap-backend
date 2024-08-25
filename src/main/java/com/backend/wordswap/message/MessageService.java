package com.backend.wordswap.message;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.gemini.GeminiAPIService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.translation.entity.TranslationModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MessageService {

	private final GeminiAPIService geminiAPIService;
	private final ConversationService conversationService;

	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final TranslationConfigurationRepository translationConfigRepository;

	public MessageService(MessageRepository messageRepository, UserRepository userRepository,
			ConversationService conversationService, GeminiAPIService geminiAPIService,
			TranslationConfigurationRepository translationConfigRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.conversationService = conversationService;
		this.geminiAPIService = geminiAPIService;
		this.translationConfigRepository = translationConfigRepository;
	}

	public List<ConversationResponseDTO> sendMessage(MessageCreateDTO dto) throws Exception {
		ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		TranslationModel translation = this.processContent(dto);

		this.saveMessage(Encrypt.encrypt(dto.getContent()), sender, conversation, translation);

		return this.conversationService.findAllConversationByUserId(dto.getSenderId());
	}

	private TranslationModel processContent(MessageCreateDTO dto) throws Exception {
		List<TranslationConfigurationModel> configs = this.translationConfigRepository
				.findAllByConversationId(dto.getConversationId());

		TranslationModel translation = new TranslationModel();
		String content = dto.getContent();

		List<TranslationConfigurationModel> senderConfigs = configs.stream()
				.filter(f -> dto.getSenderId().compareTo(f.getUser().getId()) == 0).toList();

		if (!CollectionUtils.isEmpty(senderConfigs)) {
			TranslationConfigurationModel configSender = this.getTranslation(senderConfigs, TranslationType.SENDING);
			if (Objects.nonNull(configSender)) {
				translation.setLanguageCodeSending(configSender.getTargetLanguage());

				String contentTranslated = this.geminiAPIService.translateText(content, configSender.getTargetLanguage());
				translation.setContentSending(contentTranslated);
			}
		}

		List<TranslationConfigurationModel> receiverConfigs = configs.stream()
				.filter(f -> dto.getReceiverId().compareTo(f.getUser().getId()) == 0).toList();

		if (!CollectionUtils.isEmpty(receiverConfigs)) {
			TranslationConfigurationModel configReceiver = this.getTranslation(receiverConfigs, TranslationType.RECEIVING);
			if (Objects.nonNull(configReceiver)) {
				translation.setLanguageCodeReceiver(configReceiver.getTargetLanguage());

				String contentTranslated = this.geminiAPIService.translateText(content, configReceiver.getTargetLanguage());
				translation.setContentReceiver(contentTranslated);
			}
		}

		return translation;
	}

	private TranslationConfigurationModel getTranslation(List<TranslationConfigurationModel> senderConfigs,
			TranslationType type) {
		return senderConfigs.stream().filter(f -> type.equals(f.getType()) && f.getIsActive()).findFirst().orElse(null);
	}

	private void saveMessage(String content, UserModel sender, ConversationModel conversation, TranslationModel translation) {
		MessageModel message = new MessageModel(content, sender, conversation);
		if (Objects.nonNull(translation)) {
			message.setTranslation(translation);
		}

		this.messageRepository.save(message);
	}

	public List<ConversationResponseDTO> editMessage(MessageEditDTO dto) throws Exception {
		MessageModel message = this.messageRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException("Message not found."));

		message.setContent(Encrypt.encrypt(dto.getContent()));
		message.setIsEdited(Boolean.TRUE);

		this.messageRepository.save(message);

		return this.conversationService.findAllConversationByUserId(message.getSender().getId());
	}

	public List<ConversationResponseDTO> deleteMessage(MessageDeleteDTO dto) {
		MessageModel message = this.messageRepository.findById(dto.id())
				.orElseThrow(() -> new EntityNotFoundException("Message not found."));

		message.setIsDeleted(Boolean.TRUE);

		this.messageRepository.save(message);

		return this.conversationService.findAllConversationByUserId(message.getSender().getId());
	}
}
