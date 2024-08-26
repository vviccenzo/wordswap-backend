package com.backend.wordswap.conversation.factory;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.dto.MessageRecord;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageContent;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConversationFactory {

	public ConversationResponseDTO buildMessages(Long userId, ConversationModel conv) {
		ConversationResponseDTO dto = new ConversationResponseDTO();
		dto.setId(conv.getId());
		dto.setSenderId(conv.getUserInitiator().getId());
		dto.setReceiverId(conv.getUserRecipient().getId());

		boolean isInitiator = conv.getUserInitiator().getId().equals(userId);

		Long userInitiator = conv.getUserInitiator().getId();
		Long userRecipient = conv.getUserRecipient().getId();

		Map<Long, TranslationConfigResponseDTO> configsUser = new HashMap<>();
		configsUser.put(userInitiator, this.buildTranslationConfig(userInitiator, conv));
		configsUser.put(userRecipient, this.buildTranslationConfig(userRecipient, conv));

		dto.setProfilePic(this.getProfilePic(conv, isInitiator));
		dto.setConversationName(this.getConversationName(conv, isInitiator));

		List<MessageRecord> userMessages = this.getDecryptedMessages(conv, userId, true);
		List<MessageRecord> targetUserMessages = this.getDecryptedMessages(conv, userId, false);

		dto.setConfigsUser(configsUser);
		dto.setUserMessages(userMessages);
		dto.setTargetUserMessages(targetUserMessages);
		dto.setLastMessage(this.determineLastMessage(userMessages, targetUserMessages));

		return dto;
	}

	private TranslationConfigResponseDTO buildTranslationConfig(Long userId, ConversationModel conversation) {
		TranslationConfigResponseDTO dto = new TranslationConfigResponseDTO();

		dto.setSendingTranslation(this.getTranslationTarget(conversation, userId, TranslationType.SENDING));
		dto.setReceivingTranslation(this.getTranslationTarget(conversation, userId, TranslationType.RECEIVING));
		dto.setIsSendingTranslation(this.isTranslationActive(conversation, userId, TranslationType.SENDING));
		dto.setIsReceivingTranslation(this.isTranslationActive(conversation, userId, TranslationType.RECEIVING));

		return dto;
	}

	private boolean isTranslationActive(ConversationModel conversation, Long userId, TranslationType type) {
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
	        })
	        .findFirst()
	        .orElse("");
	}

	private String getProfilePic(ConversationModel conversationModel, boolean isInitiator) {
		if (isInitiator) {
			if (Objects.nonNull(conversationModel.getUserRecipient())) {
				return this
						.convertByteArrayToBase64(conversationModel.getUserRecipient().getUserProfile().getContent());
			} else {
				return "";
			}
		} else {
			if (Objects.nonNull(conversationModel.getUserInitiator())) {
				return this
						.convertByteArrayToBase64(conversationModel.getUserInitiator().getUserProfile().getContent());
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

	private List<MessageRecord> getDecryptedMessages(ConversationModel conv, Long userId, boolean isUserMessages) {
		List<MessageModel> messages = conv.getMessages();
		List<MessageRecord> decryptedMessages = new ArrayList<>();

		for (MessageModel message : messages) {
			if (message.getSender().getId().equals(userId) == isUserMessages) {
				decryptedMessages.add(this.decryptMessage(message));
			}
		}

		return decryptedMessages;
	}

	private MessageRecord decryptMessage(MessageModel msg) {
		try {
			return MessageRecord.builder().id(msg.getId()).content(Encrypt.decrypt(msg.getContent()))
					.sender(msg.getSender().getUsername()).timeStamp(msg.getSentAt()).senderId(msg.getSender().getId())
					.isEdited(Optional.ofNullable(msg.getIsEdited()).orElse(false))
					.isDeleted(Optional.ofNullable(msg.getIsDeleted()).orElse(false))
					.messageContent(new MessageContent(Encrypt.decrypt(msg.getContent()),
							Objects.nonNull(msg.getTranslation()) ? msg.getTranslation().getContentSending()
									: Encrypt.decrypt(msg.getContent()),
							Objects.nonNull(msg.getTranslation()) ? msg.getTranslation().getContentReceiver()
									: Encrypt.decrypt(msg.getContent())))
					.build();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<LocalDateTime, String> determineLastMessage(List<MessageRecord> user, List<MessageRecord> target) {
		Map.Entry<LocalDateTime, String> lastUserMessage = getLastMessageEntry(user);
		Map.Entry<LocalDateTime, String> lastTargetUserMessage = getLastMessageEntry(target);

		if (lastUserMessage != null && lastTargetUserMessage != null) {
			LocalDateTime lastMessageKey;
			String lastMessageContent;

			if (!lastUserMessage.getKey().isBefore(lastTargetUserMessage.getKey())) {
				lastMessageKey = lastUserMessage.getKey();
				lastMessageContent = lastUserMessage.getValue();
			} else {
				lastMessageKey = lastTargetUserMessage.getKey();
				lastMessageContent = lastTargetUserMessage.getValue();
			}

			Map<LocalDateTime, String> lastMessage = new HashMap<>();
			lastMessage.put(lastMessageKey, lastMessageContent);
			return lastMessage;
		}

		return new HashMap<>();
	}

	private Map.Entry<LocalDateTime, String> getLastMessageEntry(List<MessageRecord> msg) {
		return msg.stream().reduce((first, second) -> second)
				.map(message -> Map.entry(message.getTimeStamp(), message.getContent())).orElse(null);
	}
}
