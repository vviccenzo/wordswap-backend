package com.backend.wordswap.message;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.gemini.GeminiAPIService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.dto.MessageRequestDTO;
import com.backend.wordswap.message.dto.MessageViewDTO;
import com.backend.wordswap.message.entity.MessageImageModel;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.websocket.definition.MessageTypingDTO;
import com.backend.wordswap.websocket.definition.WebSocketAction;
import com.backend.wordswap.websocket.definition.WebSocketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class MessageService {

	private final GeminiAPIService geminiAPIService;
	private final ConversationService conversationService;
	private final SimpMessagingTemplate messagingTemplate;

	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final TranslationConfigurationRepository translationConfigRepository;

	@Transactional
	public void sendMessage(MessageCreateDTO dto) throws Exception {
		try {
			ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
			UserModel sender = this.userRepository.findById(dto.getSenderId())
					.orElseThrow(EntityNotFoundException::new);

			List<TranslationConfigurationModel> userConfigs = this
					.getReceiverTranslationConfigs(dto.getConversationId(), conversation.getParticipantsIds());

			AtomicReference<String> content = new AtomicReference<>(dto.getContent());

			Boolean hasConfigs = !CollectionUtils.isEmpty(userConfigs);
			if (hasConfigs.booleanValue()) {
				this.processContent(content, userConfigs);
			}

			this.saveMessage(Encrypt.encrypt(content.get()), sender, conversation, dto, hasConfigs);
			this.sendWebSocketUpdate(conversation.getParticipantsIds());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AtomicReference<String> processContent(AtomicReference<String> content,
			List<TranslationConfigurationModel> userConfigs) throws Exception {
		String validatedContent = content.get();

		TranslationConfigurationModel configReceiver = this.getTranslationConfig(userConfigs,
				TranslationType.RECEIVING);
		TranslationConfigurationModel configImproving = this.getTranslationConfig(userConfigs,
				TranslationType.IMPROVING);

		if (StringUtils.isNotBlank(validatedContent)) {
			validatedContent = this.applyConfigs(validatedContent, configReceiver, configImproving);
		}

		content.set(validatedContent);
		return content;
	}

	public boolean isValidContent(String content) throws JsonProcessingException {
		final String VALID_MESSAGE = "Mensagem Válida";

		if (content == null || content.trim().isEmpty()) {
			log.info("Conteudo vazio");
			return false;
		}

		String response = "";
		try {
			response = this.geminiAPIService.validateContent(content);
		} catch (Exception e) {
			log.error("Ocorreu um erro inesperado");
		}

		if (response == null || response.trim().isEmpty()) {
			log.info("Retorno do Gemini vazio");
			return false;
		}

		log.info("Retorno Gemini: " + response);
		return response.trim().equalsIgnoreCase(VALID_MESSAGE);
	}

	private String applyConfigs(String content, TranslationConfigurationModel configReceiver,
			TranslationConfigurationModel configImproving) throws JsonProcessingException {
		int maxAttempts = 3;
		boolean isValid = false;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			if (this.isValidContent(content)) {
				isValid = true;
				break;
			}
		}

		if (!isValid) {
			throw new RuntimeException("Envie uma mensagem válida, caso queira utilizar funções que usam Inteligência Artificial.");
		}

		try {
			content = this.applyTranslationOrImprovement(configImproving, configReceiver, content);
		} catch (Exception e) {
			log.error("Ocorreu um erro inesperado");
		}

		return content;
	}

	private String applyTranslationOrImprovement(TranslationConfigurationModel configImproving,
			TranslationConfigurationModel configReceiver, String content) throws JsonProcessingException {
		if (configImproving != null && Boolean.TRUE.equals(configImproving.getIsActive())) {
			content = this.geminiAPIService.improveText(content);
		}

		if (configReceiver != null && Boolean.TRUE.equals(configReceiver.getIsActive())) {
			content = this.geminiAPIService.translateText(content, configReceiver.getTargetLanguage());
		}

		return content;
	}

	public List<TranslationConfigurationModel> getReceiverTranslationConfigs(Long conversationId, Set<Long> userIds) {
		return this.translationConfigRepository.findAllByConversationIdAndUserIdIn(conversationId, userIds).stream()
				.toList();
	}

	public TranslationConfigurationModel getTranslationConfig(List<TranslationConfigurationModel> configs,
			TranslationType type) {
		return configs.stream().filter(config -> type.equals(config.getType()) && config.getIsActive()).findFirst()
				.orElse(null);
	}

	private void saveMessage(String encryptedContent, UserModel sender, ConversationModel conversation,
			MessageCreateDTO dto, Boolean hasConfigs) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		MessageModel message = new MessageModel(encryptedContent, sender, conversation);
		if (hasConfigs.booleanValue()) {
			message.setContentOriginal(Encrypt.encrypt(dto.getContent()));
		}

		if (dto.getImageContent() != null) {
			byte[] imageContent = Base64.getDecoder().decode(dto.getImageContent());
			MessageImageModel image = new MessageImageModel(message, imageContent, dto.getImageFileName(),
					LocalDate.now());
			message.setImage(image);
		}

		this.messageRepository.save(message);
	}

	@Transactional
	public void editMessage(MessageEditDTO dto) throws Exception {
		MessageModel message = this.getMessageById(dto.getId());
		ConversationModel conversation = message.getConversation();

		message.setIsEdited(Boolean.TRUE);

		List<TranslationConfigurationModel> userConfigs = this.getReceiverTranslationConfigs(conversation.getId(),
				conversation.getParticipantsIds());
		AtomicReference<String> content = new AtomicReference<>(dto.getContent());
		if (!CollectionUtils.isEmpty(userConfigs)) {
			this.processContent(content, userConfigs);
		}

		message.setContent(Encrypt.encrypt(content.get()));

		this.messageRepository.save(message);

		this.sendWebSocketUpdate(conversation.getParticipantsIds());
	}

	@Transactional
	public void deleteMessage(MessageDeleteDTO dto) {
		MessageModel message = this.getMessageById(dto.id());
		message.setIsDeleted(Boolean.TRUE);

		this.messageRepository.save(message);
	}

	private MessageModel getMessageById(Long messageId) {
		return this.messageRepository.findById(messageId)
				.orElseThrow(() -> new EntityNotFoundException("Message not found."));
	}

	public void sendWebSocketUpdate(Set<Long> userIds) {
		userIds.forEach(userId -> {
			List<ConversationResponseDTO> conversations = this.conversationService.findAllConversationByUserId(userId,
					0);
			this.messagingTemplate.convertAndSend("/topic/messages/" + userId,
					new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, conversations));
		});
	}

	public ConversationResponseDTO getMessages(MessageRequestDTO dto) {
		return this.conversationService.findAllConversationByUserId(dto.getUserId(), dto.getPageNumber()).stream()
				.filter(conv -> conv.getId().equals(dto.getConversationId())).findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
	}

	@Transactional
	public void viewMessages(MessageViewDTO dto) {
		List<MessageModel> messages = this.messageRepository.findAllById(dto.getMessageIds());
		this.messageRepository.saveAll(messages.stream().map(msg -> {
			msg.setViewed(Boolean.TRUE);
			msg.setViewedAt(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());

			return msg;
		}).toList());

		Set<Long> participantIds = messages.stream().flatMap(msg -> msg.getConversation().getParticipants().stream())
				.map(UserModel::getId).collect(Collectors.toSet());

		this.sendWebSocketUpdate(participantIds);
	}

	public void typingMessage(MessageTypingDTO messageTypingDTO) {
		// TODO Auto-generated method stub

	}
}
