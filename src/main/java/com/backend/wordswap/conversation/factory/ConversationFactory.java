package com.backend.wordswap.conversation.factory;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.dto.MessageRecord;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageContent;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.translation.entity.TranslationModel;

import lombok.experimental.UtilityClass;

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

@UtilityClass
public class ConversationFactory {

	public static ConversationResponseDTO buildMessages(Long userId, ConversationModel conv, Map<Long, List<MessageModel>> messageByConversation, Map<Long, Long> totalMessagesByConversation) {
		ConversationResponseDTO dto = new ConversationResponseDTO();
		dto.setId(conv.getId());
		dto.setSenderId(conv.getUserInitiator().getId());
		dto.setReceiverId(conv.getUserRecipient().getId());
		dto.setReceiverCode(conv.getUserRecipient().getUserCode());
		dto.setSenderCode(conv.getUserInitiator().getUserCode());

		boolean isInitiator = conv.getUserInitiator().getId().equals(userId);

		Long userInitiator = conv.getUserInitiator().getId();
		Long userRecipient = conv.getUserRecipient().getId();

		Map<Long, TranslationConfigResponseDTO> configsUser = new HashMap<>();
		configsUser.put(userInitiator, buildTranslationConfig(userInitiator, conv));
		configsUser.put(userRecipient, buildTranslationConfig(userRecipient, conv));

		dto.setProfilePic(getProfilePic(conv, isInitiator));
		dto.setConversationName(getConversationName(conv, isInitiator));
		dto.setTotalMessages(totalMessagesByConversation.get(conv.getId()).intValue());

		List<MessageRecord> userMessages = getDecryptedMessages(conv, userId, true, messageByConversation);
		List<MessageRecord> targetUserMessages = getDecryptedMessages(conv, userId, false, messageByConversation);

		dto.setConfigsUser(configsUser);
		dto.setUserMessages(userMessages);
		dto.setTargetUserMessages(targetUserMessages);
		dto.setLastMessage(determineLastMessage(userMessages, targetUserMessages));

		return dto;
	}

	private TranslationConfigResponseDTO buildTranslationConfig(Long userId, ConversationModel conversation) {
		TranslationConfigResponseDTO dto = new TranslationConfigResponseDTO();

		dto.setReceivingTranslation(getTranslationTarget(conversation, userId, TranslationType.RECEIVING));
		dto.setIsReceivingTranslation(isActive(conversation, userId, TranslationType.RECEIVING));
		dto.setIsImprovingText(isActive(conversation, userId, TranslationType.IMPROVING));

		return dto;
	}

	private boolean isActive(ConversationModel conversation, Long userId, TranslationType type) {
		return conversation.getTranslationConfigurations().stream()
				.anyMatch(config -> config.getUser().getId().equals(userId) && config.getType().equals(type)
						&& config.getIsActive());
	}

	private String getTranslationTarget(ConversationModel conversation, Long userId, TranslationType type) {
		return conversation.getTranslationConfigurations().stream()
				.filter(config -> config.getUser().getId().equals(userId) && config.getType().equals(type))
				.map(trans -> {
					String[] parts = trans.getTargetLanguage().split(" - ");
					return parts.length > 1 ? parts[1] : "";
				}).findFirst().orElse("");
	}

	private String getProfilePic(ConversationModel conversationModel, boolean isInitiator) {
		if (isInitiator) {
			if (Objects.nonNull(conversationModel.getUserRecipient())
					&& Objects.nonNull(conversationModel.getUserRecipient().getUserProfile())) {
				if (Objects.nonNull(conversationModel.getUserRecipient().getUserProfile().getContent())) {
					return convertByteArrayToBase64(conversationModel.getUserRecipient().getUserProfile().getContent());
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else {
			if (Objects.nonNull(conversationModel.getUserInitiator())) {
				if (Objects.nonNull(conversationModel.getUserInitiator().getUserProfile())
						&& Objects.nonNull(conversationModel.getUserInitiator().getUserProfile().getContent())) {
					return convertByteArrayToBase64(conversationModel.getUserInitiator().getUserProfile().getContent());
				} else {
					return "";
				}
			} else {
				return "";
			}
		}
	}

	public String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	private String getConversationName(ConversationModel conv, boolean isInitiator) {
		return isInitiator ? conv.getUserRecipient().getName() : conv.getUserInitiator().getName();
	}

	private List<MessageRecord> getDecryptedMessages(ConversationModel conv, Long userId, boolean isUserMessages, Map<Long, List<MessageModel>> messageByConversation) {
		List<MessageModel> messages = messageByConversation.getOrDefault(conv.getId(), new ArrayList<>());
		List<MessageRecord> decryptedMessages = new ArrayList<>();

		for (MessageModel message : messages) {
			if (message.getSender().getId().equals(userId) == isUserMessages) {
				decryptedMessages.add(decryptMessage(message));
			}
		}

		return decryptedMessages;
	}

	private MessageRecord decryptMessage(MessageModel msg) {
		try {
			String decryptedContent = Encrypt.decrypt(msg.getContent());
			String contentReceiver = Optional.ofNullable(msg.getTranslation()).map(TranslationModel::getContentReceiver).orElse(decryptedContent);
			MessageContent messageContent = new MessageContent(decryptedContent, contentReceiver);

			return MessageRecord.builder()
					.id(msg.getId())
					.content(decryptedContent)
					.sender(msg.getSender().getUsername())
					.timeStamp(msg.getSentAt()).senderId(msg.getSender().getId())
					.isEdited(Optional.ofNullable(msg.getIsEdited()).orElse(false))
					.isDeleted(Optional.ofNullable(msg.getIsDeleted()).orElse(false))
					.messageContent(messageContent)
					.build();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<LocalDateTime, String> determineLastMessage(List<MessageRecord> user, List<MessageRecord> target) {
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

	private static Map<LocalDateTime, String> createLastMessageMap(Map.Entry<LocalDateTime, String> lastMessage) {
		Map<LocalDateTime, String> messageMap = new HashMap<>();
		if (lastMessage != null) {
			messageMap.put(lastMessage.getKey(), lastMessage.getValue());
		}

		return messageMap;
	}

	private Map.Entry<LocalDateTime, String> getLastMessageEntry(List<MessageRecord> msg) {
		return msg.stream().max(Comparator.comparing(MessageRecord::getTimeStamp))
				.map(message -> Map.entry(message.getTimeStamp(), message.getContent())).orElse(null);
	}
}
