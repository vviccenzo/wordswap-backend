package com.backend.wordswap.conversation.factory;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.dto.MessageRecord;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConversationFactory {

    public ConversationResponseDTO buildMessages(Long userId, ConversationModel conversationModel) {
        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setId(conversationModel.getId());
        dto.setSenderId(userId);

        if(conversationModel.getUserInitiator().getId().compareTo(userId) == 0) {
        	dto.setProfilePic(conversationModel.getUserRecipient().getUserProfile().getContent());
            dto.setConversationName(conversationModel.getUserRecipient().getUsername());
        } else {
        	dto.setProfilePic(conversationModel.getUserInitiator().getUserProfile().getContent());
            dto.setConversationName(conversationModel.getUserInitiator().getUsername());
        }

        List<MessageRecord> userMessages = conversationModel.getMessages().stream()
                .filter(message -> message.getSender().getId().compareTo(userId) == 0)
                .map(message -> {
					try {
						return new MessageRecord(message.getId(),  Encrypt.decrypt(message.getContent()), message.getSender().getUsername(), message.getSentAt(), message.getSender().getId());
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
						throw new RuntimeException(e);
					}
				})
                .toList();

        List<MessageRecord> targetUserMessages = conversationModel.getMessages().stream()
                .filter(message -> message.getSender().getId().compareTo(userId) != 0)
                .map(message -> {
					try {
						return new MessageRecord(message.getId(),  Encrypt.decrypt(message.getContent()), message.getSender().getUsername(), message.getSentAt(), message.getSender().getId());
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
						throw new RuntimeException(e);
					}    
				})
                .toList();

        dto.setUserMessages(userMessages);
        dto.setTargetUserMessages(targetUserMessages);

        Map.Entry<LocalDateTime, String> lastUserMessage = userMessages.stream()
                .reduce((first, second) -> second)
                .map(message -> Map.entry(message.timestamp(), message.text()))
                .orElse(null);

        Map.Entry<LocalDateTime, String> lastTargetUserMessage = targetUserMessages.stream()
                .reduce((first, second) -> second)
                .map(message -> Map.entry(message.timestamp(), message.text()))
                .orElse(null);

        if (lastUserMessage != null && lastTargetUserMessage != null) {
            Map<LocalDateTime, String> lastMessage = new HashMap<>();
            LocalDateTime lastMessageKey = !lastUserMessage.getKey().isBefore(lastTargetUserMessage.getKey()) ? lastUserMessage.getKey() : lastTargetUserMessage.getKey();
            String lastMessageContent = !lastUserMessage.getKey().isBefore(lastTargetUserMessage.getKey()) ? lastUserMessage.getValue() : lastTargetUserMessage.getValue();
            lastMessage.put(lastMessageKey, lastMessageContent);
            dto.setLastMessage(lastMessage);
        }

        return dto;
    }

}
