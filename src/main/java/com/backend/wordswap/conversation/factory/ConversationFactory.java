package com.backend.wordswap.conversation.factory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageContent;
import com.backend.wordswap.message.dto.MessageRecord;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.entity.UserModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConversationFactory {
	
	public static List<ConversationResponseDTO> buildConversationsResponse(UserModel user, Map<Long, List<MessageModel>> messageByConversation, Long userId, Map<Long, Long> totalMessagesByConversation) {
		List<ConversationResponseDTO> conversationResponseDTOS = new ArrayList<>();
		user.getInitiatedConversations().stream().filter(conversation -> !conversation.getIsDeletedInitiator())
				.map(conversation -> ConversationFactory.buildMessages(userId, conversation, messageByConversation, totalMessagesByConversation))
				.forEach(conversationResponseDTOS::add);

		user.getReceivedConversations().stream().filter(conversation -> !conversation.getIsDeletedRecipient())
				.map(conversation -> ConversationFactory.buildMessages(userId, conversation, messageByConversation, totalMessagesByConversation))
				.forEach(conversationResponseDTOS::add);

		conversationResponseDTOS.sort((c1, c2) -> {
			LocalDateTime lastMessageC1 = c1.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);
			LocalDateTime lastMessageC2 = c2.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);

			return lastMessageC2.compareTo(lastMessageC1);
		});

		return conversationResponseDTOS;
	}

	public static ConversationResponseDTO buildMessages(Long userId, ConversationModel conv, Map<Long, List<MessageModel>> messageByConversation, Map<Long, Long> totalMessagesByConversation) {
		ConversationResponseDTO dto = new ConversationResponseDTO();
		dto.setId(conv.getId());
		dto.setSenderId(conv.getUserInitiator().getId());
		dto.setReceiverId(conv.getUserRecipient().getId());
		dto.setReceiverCode(conv.getUserRecipient().getUserCode());
		dto.setSenderCode(conv.getUserInitiator().getUserCode());
		dto.setIsArchivedInitiator(conv.isArchivedInitiator());
		dto.setIsArchivedRecipient(conv.isArchivedRecipient());

		boolean isInitiator = conv.getUserInitiator().getId().equals(userId);
        Long userInitiator = conv.getUserInitiator().getId();
        Long userRecipient = conv.getUserRecipient().getId();

        Map<Long, TranslationConfigResponseDTO> configsUser = new HashMap<>();
        configsUser.put(userInitiator, buildTranslationConfig(userInitiator, conv));
        configsUser.put(userRecipient, buildTranslationConfig(userRecipient, conv));

        dto.setConfigsUser(configsUser);
		dto.setProfilePic(getProfilePic(conv, isInitiator));
		dto.setUserInfo(buildInfo(conv, isInitiator));
		dto.setConversationName(getConversationName(conv, isInitiator));
		dto.setTotalMessages(totalMessagesByConversation.getOrDefault(conv.getId(), 0L).intValue());
		
		List<MessageRecord> userMessages = getDecryptedMessages(conv, userId, true, messageByConversation);
		List<MessageRecord> targetUserMessages = getDecryptedMessages(conv, userId, false, messageByConversation);

		dto.setUserMessages(userMessages);
		dto.setTargetUserMessages(targetUserMessages);
		dto.setLastMessage(determineLastMessage(userMessages, targetUserMessages));

		return dto;
	}
	
	public static UserDTO buildInfo(ConversationModel conversationModel, boolean isInitiator) {
		return isInitiator ? new UserDTO(conversationModel.getUserRecipient()) : new UserDTO(conversationModel.getUserInitiator());
	}

	public static String getProfilePic(ConversationModel conversationModel, boolean isInitiator) {
	    UserModel user = isInitiator ? conversationModel.getUserRecipient() : conversationModel.getUserInitiator();

	    if (Objects.nonNull(user) && Objects.nonNull(user.getUserProfile()) && Objects.nonNull(user.getUserProfile().getContent())) {
	        return convertByteArrayToBase64(user.getUserProfile().getContent());
	    }
	    
	    return "";
	}

	public static String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	public static String getConversationName(ConversationModel conv, boolean isInitiator) {
		return isInitiator ? conv.getUserRecipient().getName() : conv.getUserInitiator().getName();
	}

	public static List<MessageRecord> getDecryptedMessages(ConversationModel conv, Long userId, boolean isUserMessages, Map<Long, List<MessageModel>> messageByConversation) {
		List<MessageModel> messages = messageByConversation.getOrDefault(conv.getId(), new ArrayList<>());
		List<MessageRecord> decryptedMessages = new ArrayList<>();

		for (MessageModel message : messages) {
			if (message.getSender().getId().equals(userId) == isUserMessages) {
				decryptedMessages.add(decryptMessage(message));
			}
		}

		return decryptedMessages;
	}

	public static MessageRecord decryptMessage(MessageModel msg) {
		try {
			String decryptedContent = Encrypt.decrypt(msg.getContent());
			MessageContent messageContent = new MessageContent(decryptedContent, decryptedContent);
			String image = msg.getImage() != null && msg.getImage().getContent() != null ? Base64.getEncoder().encodeToString(msg.getImage().getContent()) : "";

			return MessageRecord.builder()
					.id(msg.getId())
					.content(decryptedContent)
					.sender(msg.getSender().getUsername())
					.timeStamp(msg.getSentAt()).senderId(msg.getSender().getId())
					.isEdited(Optional.ofNullable(msg.getIsEdited()).orElse(false))
					.isDeleted(Optional.ofNullable(msg.getIsDeleted()).orElse(false))
					.messageContent(messageContent)
					.image(image)
					.build();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<LocalDateTime, String> determineLastMessage(List<MessageRecord> user, List<MessageRecord> target) {
		Map.Entry<LocalDateTime, String> lastUserMessage = getLastMessageEntry(user);
		Map.Entry<LocalDateTime, String> lastTargetUserMessage = getLastMessageEntry(target);

		if (lastUserMessage == null) {
			return createLastMessageMap(lastTargetUserMessage);
		}

		if (lastTargetUserMessage == null) {
			return createLastMessageMap(lastUserMessage);
		}

		Map.Entry<LocalDateTime, String> lastMessage = lastUserMessage.getKey().isAfter(lastTargetUserMessage.getKey())
				? lastUserMessage
				: lastTargetUserMessage;

		return createLastMessageMap(lastMessage);
	}

	public static Map<LocalDateTime, String> createLastMessageMap(Map.Entry<LocalDateTime, String> lastMessage) {
		Map<LocalDateTime, String> messageMap = new HashMap<>();
		if (lastMessage != null) {
			messageMap.put(lastMessage.getKey(), lastMessage.getValue());
		}

		return messageMap;
	}

	public static Map.Entry<LocalDateTime, String> getLastMessageEntry(List<MessageRecord> msg) {
		return msg.stream().max(Comparator.comparing(MessageRecord::getTimeStamp))
				.map(message -> Map.entry(message.getTimeStamp(), message.getContent())).orElse(null);
	}

	public static TranslationConfigResponseDTO buildTranslationConfig(Long userId, ConversationModel conversation) {
		TranslationConfigResponseDTO dto = new TranslationConfigResponseDTO();

		dto.setReceivingTranslation(getTranslationTarget(conversation, userId, TranslationType.RECEIVING));
		dto.setIsReceivingTranslation(isActive(conversation, userId, TranslationType.RECEIVING));
		dto.setIsImprovingText(isActive(conversation, userId, TranslationType.IMPROVING));

		return dto;
	}

	public static boolean isActive(ConversationModel conversation, Long userId, TranslationType type) {
		return conversation.getTranslationConfigurations().stream()
				.anyMatch(config -> config.getUser().getId().equals(userId) && config.getType().equals(type)
						&& config.getIsActive());
	}

	public static String getTranslationTarget(ConversationModel conversation, Long userId, TranslationType type) {
		return conversation.getTranslationConfigurations().stream()
				.filter(config -> config.getUser().getId().equals(userId) && config.getType().equals(type))
				.map(trans -> {
					String[] parts = trans.getTargetLanguage().split(" - ");
					return parts.length > 1 ? parts[1] : "";
				}).findFirst().orElse("");
	}

}
