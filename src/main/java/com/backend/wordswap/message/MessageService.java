package com.backend.wordswap.message;

import java.util.List;

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
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.translation.entity.TranslationModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.websocket.WebSocketAction;
import com.backend.wordswap.websocket.WebSocketResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MessageService {

    private final GeminiAPIService geminiAPIService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final TranslationConfigurationRepository translationConfigRepository;

	public MessageService(MessageRepository messageRepository, UserRepository userRepository,
			ConversationService conversationService, GeminiAPIService geminiAPIService,
			TranslationConfigurationRepository translationConfigRepository, SimpMessagingTemplate messagingTemplate) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.conversationService = conversationService;
		this.geminiAPIService = geminiAPIService;
		this.translationConfigRepository = translationConfigRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public void sendMessage(MessageCreateDTO dto) throws Exception {
		ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		List<TranslationConfigurationModel> receiverConfigs = this.getReceiverTranslationConfigs(dto.getConversationId(), dto.getReceiverId());
		List<TranslationConfigurationModel> senderConfigs = this.getReceiverTranslationConfigs(dto.getConversationId(), dto.getSenderId());

		String content = dto.getContent();
		if (!CollectionUtils.isEmpty(senderConfigs)) {
			content = this.processImprovingTranslation(content, senderConfigs);
		}

		TranslationModel translation = this.processContent(content, receiverConfigs);

		this.saveMessage(Encrypt.encrypt(content), sender, conversation, translation);
		this.sendWebSocketUpdate(dto.getSenderId(), dto.getReceiverId());
	}

	private TranslationModel processContent(String content, List<TranslationConfigurationModel> receiverConfigs) throws Exception {
	    TranslationModel translation = new TranslationModel();

	    if (!CollectionUtils.isEmpty(receiverConfigs)) {
	    	this.processReceiverTranslation(content, translation, receiverConfigs);
	    }

	    return translation;
	}

	private void processReceiverTranslation(String content, TranslationModel translation, List<TranslationConfigurationModel> receiverConfigs) {
	    TranslationConfigurationModel configReceiver = this.getTranslationConfig(receiverConfigs, TranslationType.RECEIVING);

	    if (configReceiver != null && Boolean.TRUE.equals(configReceiver.getIsActive())) {
	        translation.setLanguageCodeReceiver(configReceiver.getTargetLanguage());

	        try {
	            String translatedText = this.geminiAPIService.translateText(content, configReceiver.getTargetLanguage());
	            translation.setContentReceiver(translatedText);
	        } catch (Exception e) {
	            e.printStackTrace();
	            translation.setContentReceiver(content);
	        }
	    }
	}

	private String processImprovingTranslation(String content, List<TranslationConfigurationModel> receiverConfigs) {
	    TranslationConfigurationModel configImproving = this.getTranslationConfig(receiverConfigs, TranslationType.IMPROVING);
	    String contentImproved = content;

	    if (configImproving != null && configImproving.getIsActive()) {
	        try {
	        	contentImproved = this.geminiAPIService.improveText(content);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return contentImproved;
	}

	private List<TranslationConfigurationModel> getReceiverTranslationConfigs(Long conversationId, Long receiverId) {
		return this.translationConfigRepository.findAllByConversationId(conversationId).stream()
				.filter(config -> receiverId.equals(config.getUser().getId())).toList();
	}

	private TranslationConfigurationModel getTranslationConfig(List<TranslationConfigurationModel> configs, TranslationType type) {
		return configs.stream().filter(config -> type.equals(config.getType()) && config.getIsActive()).findFirst()
				.orElse(null);
	}

    private void saveMessage(String encryptedContent, UserModel sender, ConversationModel conversation, TranslationModel translation) {
        MessageModel message = new MessageModel(encryptedContent, sender, conversation);
        if (translation != null) {
            message.setTranslation(translation);
        }

        this.messageRepository.save(message);
    }

    @Transactional
    public void editMessage(MessageEditDTO dto) throws Exception {
        MessageModel message = getMessageById(dto.getId());
        message.setContent(Encrypt.encrypt(dto.getContent()));
        message.setIsEdited(Boolean.TRUE);

        this.messageRepository.save(message);
        this.sendWebSocketUpdate(message.getConversation().getUserInitiator().getId(), message.getConversation().getUserRecipient().getId());
    }

    @Transactional
    public void deleteMessage(MessageDeleteDTO dto) {
        MessageModel message = getMessageById(dto.id());
        message.setIsDeleted(Boolean.TRUE);

        this.messageRepository.save(message);
        this.sendWebSocketUpdate(message.getConversation().getUserInitiator().getId(), message.getConversation().getUserRecipient().getId());
    }

    private MessageModel getMessageById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found."));
    }

    private void sendWebSocketUpdate(Long senderId, Long receiverId) {
        List<ConversationResponseDTO> convsSender = this.conversationService.findAllConversationByUserId(senderId, 0);
        List<ConversationResponseDTO> convsTarget = this.conversationService.findAllConversationByUserId(receiverId, 0);

        this.messagingTemplate.convertAndSend("/topic/messages/" + senderId, new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsSender));
        this.messagingTemplate.convertAndSend("/topic/messages/" + receiverId, new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsTarget));
    }

	public ConversationResponseDTO getMessages(MessageRequestDTO dto) {
		return conversationService.findAllConversationByUserId(dto.getUserId(), dto.getPageNumber()).stream().filter(conv -> conv.getId().equals(dto.getConversationId())).findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
	}
}
