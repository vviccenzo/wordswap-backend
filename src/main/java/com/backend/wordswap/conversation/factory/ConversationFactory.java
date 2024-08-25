package com.backend.wordswap.conversation.factory;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.dto.MessageRecord;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageContent;
import com.backend.wordswap.message.entity.MessageModel;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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

	public ConversationResponseDTO buildMessages(Long userId, ConversationModel conversationModel) {
		ConversationResponseDTO dto = new ConversationResponseDTO();
		dto.setId(conversationModel.getId());
		dto.setSenderId(userId);
		dto.setReceiverId(conversationModel.getUserRecipient().getId());

		boolean isInitiator = conversationModel.getUserInitiator().getId().equals(userId);

		dto.setProfilePic(getProfilePic(conversationModel, isInitiator));
		dto.setConversationName(getConversationName(conversationModel, isInitiator));

		List<MessageRecord> userMessages = this.getDecryptedMessages(conversationModel, userId, true);
		List<MessageRecord> targetUserMessages = this.getDecryptedMessages(conversationModel, userId, false);

		dto.setUserMessages(userMessages);
		dto.setTargetUserMessages(targetUserMessages);
		dto.setLastMessage(determineLastMessage(userMessages, targetUserMessages));

		return dto;
	}

	private String getProfilePic(ConversationModel conversationModel, boolean isInitiator) {
		if (isInitiator) {
			if (Objects.nonNull(conversationModel.getUserRecipient())) {
				return this
						.convertByteArrayToBase64(conversationModel.getUserInitiator().getUserProfile().getContent());
			} else {
				return "";
			}
		} else {
			if (Objects.nonNull(conversationModel.getUserInitiator())) {
				return this
						.convertByteArrayToBase64(conversationModel.getUserRecipient().getUserProfile().getContent());
			} else {
				return "";
			}
		}
	}

	public String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	private String getConversationName(ConversationModel conversationModel, boolean isInitiator) {
		return isInitiator ? conversationModel.getUserRecipient().getUsername()
				: conversationModel.getUserInitiator().getUsername();
	}

	private List<MessageRecord> getDecryptedMessages(ConversationModel conversationModel, Long userId, boolean isUserMessages) {
		return conversationModel.getMessages().stream()
				.filter(message -> (message.getSender().getId().equals(userId)) == isUserMessages)
				.map(this::decryptMessage).toList();
	}

	private MessageRecord decryptMessage(MessageModel message) {
		try {
			return MessageRecord.builder().id(message.getId()).content(Encrypt.decrypt(message.getContent()))
					.sender(message.getSender().getUsername()).timeStamp(message.getSentAt())
					.senderId(message.getSender().getId())
					.isEdited(Optional.ofNullable(message.getIsEdited()).orElse(false))
					.isDeleted(Optional.ofNullable(message.getIsDeleted()).orElse(false))
					.messageContent(new MessageContent(Encrypt.decrypt(message.getContent()),
							Objects.nonNull(message.getTranslation()) ? message.getTranslation().getContentSending()
									: Encrypt.decrypt(message.getContent()),
							Objects.nonNull(message.getTranslation()) ? message.getTranslation().getContentReceiver()
									: Encrypt.decrypt(message.getContent())))
					.build();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<LocalDateTime, String> determineLastMessage(List<MessageRecord> userMessages,
			List<MessageRecord> targetUserMessages) {
		Map.Entry<LocalDateTime, String> lastUserMessage = getLastMessageEntry(userMessages);
		Map.Entry<LocalDateTime, String> lastTargetUserMessage = getLastMessageEntry(targetUserMessages);

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

	private Map.Entry<LocalDateTime, String> getLastMessageEntry(List<MessageRecord> messages) {
		return messages.stream().reduce((first, second) -> second)
				.map(message -> Map.entry(message.getTimeStamp(), message.getContent())).orElse(null);
	}
}
