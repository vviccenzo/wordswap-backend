package com.backend.wordswap.message;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
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
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.websocket.WebSocketAction;
import com.backend.wordswap.websocket.WebSocketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

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
		ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		List<TranslationConfigurationModel> receiverConfigs = this.getReceiverTranslationConfigs(dto.getConversationId(), dto.getReceiverId());
		List<TranslationConfigurationModel> senderConfigs = this.getReceiverTranslationConfigs(dto.getConversationId(), dto.getSenderId());

		AtomicReference<String> content = new AtomicReference<>(dto.getContent());

		Boolean hasConfigs = !CollectionUtils.isEmpty(senderConfigs) || !CollectionUtils.isEmpty(receiverConfigs);
		if (hasConfigs.booleanValue()) {
			this.processContent(content, receiverConfigs, senderConfigs, conversation);
		}

		this.saveMessage(Encrypt.encrypt(content.get()), sender, conversation, dto);
		this.sendWebSocketUpdate(dto.getSenderId(), dto.getReceiverId());
	}

	private AtomicReference<String> processContent(AtomicReference<String> content, 
			List<TranslationConfigurationModel> receiverConfigs, List<TranslationConfigurationModel> senderConfigs, ConversationModel conv) throws Exception 
	{
	    String validatedContent = content.get();
	    String lastMessages = conv.getMessages().stream()
	    	    .sorted(Comparator.comparing(MessageModel::getSentAt).reversed())
	    	    .limit(5)
	    	    .map(msg -> {
	    	        try {
	    	            return Encrypt.decrypt(msg.getContent());
	    	        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
	    	            e.printStackTrace();
	    	        }

	    	        return "";
	    	    })
	    	    .collect(Collectors.joining(","));

	    TranslationConfigurationModel configReceiver = this.getTranslationConfig(receiverConfigs, TranslationType.RECEIVING);
	    TranslationConfigurationModel configImproving = this.getTranslationConfig(senderConfigs, TranslationType.IMPROVING);

	    if(StringUtils.isNotBlank(validatedContent)) {
	    	validatedContent = this.doGeminiConfigs(content, validatedContent, lastMessages, configReceiver, configImproving);
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

	    String response = this.geminiAPIService.validateContent(content);
	    if (response == null || response.trim().isEmpty()) {
	    	log.info("Retorno do Gemini vazio");
	        return false;
	    }

	    log.info("Retorno Gemini: " + response);
	    return response.trim().equalsIgnoreCase(VALID_MESSAGE);
	}

	private String doGeminiConfigs(AtomicReference<String> content, String validatedContent, String lastMessages, TranslationConfigurationModel configReceiver, 
			TranslationConfigurationModel configImproving) throws JsonProcessingException 
	{
		int maxAttempts = 3;
		boolean isValid = false;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
		    if (this.isValidContent(content.get())) {
		        isValid = true;
		        break;
		    }
		}

		if (!isValid) {
		    throw new RuntimeException("Envie uma mensagem válida, caso queira utilizar funções que usam Inteligência Artificial.");
		}

		validatedContent = this.improveContentIfActive(configImproving, validatedContent, lastMessages);
		if (StringUtils.isBlank(validatedContent)) {
			validatedContent = content.get();
		}
		
		validatedContent = this.translateContentIfActive(configReceiver, validatedContent, lastMessages);
		if (StringUtils.isBlank(validatedContent)) {
			validatedContent = content.get();
		}

		return validatedContent;
	}

	public String improveContentIfActive(TranslationConfigurationModel config, String content, String context) {
	    if (config != null && Boolean.TRUE.equals(config.getIsActive())) {
	        try {
	            return this.geminiAPIService.improveText(content, context);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return content;
	}

	public String translateContentIfActive(TranslationConfigurationModel config, String content, String context) {
	    if (config != null && Boolean.TRUE.equals(config.getIsActive())) {
	        try {
	            return this.geminiAPIService.translateText(content, config.getTargetLanguage(), context);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return content;
	}

	public List<TranslationConfigurationModel> getReceiverTranslationConfigs(Long conversationId, Long receiverId) {
		return this.translationConfigRepository.findAllByConversationIdAndUserId(conversationId, receiverId).stream().toList();
	}

	public TranslationConfigurationModel getTranslationConfig(List<TranslationConfigurationModel> configs, TranslationType type) {
		return configs.stream()
				.filter(config -> type.equals(config.getType()) && config.getIsActive())
				.findFirst()
				.orElse(null);
	}

    private void saveMessage(String encryptedContent, UserModel sender, ConversationModel conversation, MessageCreateDTO dto) {
        MessageModel message = new MessageModel(encryptedContent, sender, conversation);

        if (dto.getImageContent() != null) {
        	byte[] imageContent = Base64.getDecoder().decode(dto.getImageContent());
            MessageImageModel image = new MessageImageModel(message, imageContent, dto.getImageFileName(), LocalDate.now());
            message.setImage(image);
        }

        this.messageRepository.save(message);
    }

	@Transactional
	public void editMessage(MessageEditDTO dto) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		MessageModel message = this.getMessageById(dto.getId());
		ConversationModel conversation = message.getConversation();
		message.setIsEdited(Boolean.TRUE);

		List<TranslationConfigurationModel> receiverConfigs = this.getReceiverTranslationConfigs(
			conversation.getId(), conversation.getUserRecipient().getId()
		);

		List<TranslationConfigurationModel> senderConfigs = this.getReceiverTranslationConfigs(
			conversation.getId(), conversation.getUserInitiator().getId()
		);

		AtomicReference<String> content = new AtomicReference<>(dto.getContent());
		if (!CollectionUtils.isEmpty(senderConfigs) || !CollectionUtils.isEmpty(receiverConfigs)) {
			try {
				this.processContent(content, receiverConfigs, senderConfigs, conversation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		message.setContent(Encrypt.encrypt(content.get()));

		this.messageRepository.save(message);

		this.sendWebSocketUpdate(
			message.getConversation().getUserInitiator().getId(), message.getConversation().getUserRecipient().getId()
		);
	}

    @Transactional
    public void deleteMessage(MessageDeleteDTO dto) {
        MessageModel message = this.getMessageById(dto.id());
        message.setIsDeleted(Boolean.TRUE);

        this.messageRepository.save(message);
        this.sendWebSocketUpdate(message.getConversation().getUserInitiator().getId(), message.getConversation().getUserRecipient().getId());
    }

    private MessageModel getMessageById(Long messageId) {
        return this.messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found."));
    }

    public void sendWebSocketUpdate(Long senderId, Long receiverId) {
        List<ConversationResponseDTO> convsSender = this.conversationService.findAllConversationByUserId(senderId, 0);
        List<ConversationResponseDTO> convsTarget = this.conversationService.findAllConversationByUserId(receiverId, 0);

        this.messagingTemplate.convertAndSend("/topic/messages/" + senderId, new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsSender));
        this.messagingTemplate.convertAndSend("/topic/messages/" + receiverId, new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsTarget));
    }

	public ConversationResponseDTO getMessages(MessageRequestDTO dto) {
		return this.conversationService.findAllConversationByUserId(dto.getUserId(), dto.getPageNumber()).stream()
				.filter(conv -> conv.getId().equals(dto.getConversationId())).findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
	}
}
