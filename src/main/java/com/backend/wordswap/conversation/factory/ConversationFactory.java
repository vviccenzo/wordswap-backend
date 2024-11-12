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
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.exception.ConvervationMessageBuildException;
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

	public static List<ConversationResponseDTO> buildConversationsResponse(UserModel user,
			Map<Long, List<MessageModel>> messageByConversation, Long userId,
			Map<Long, Long> totalMessagesByConversation) 
	{

		List<ConversationResponseDTO> conversationResponseDTOS = new ArrayList<>();

		user.getConversations().stream().map(conversation -> 
				ConversationFactory.buildMessages(userId, conversation, messageByConversation, totalMessagesByConversation))
		.forEach(conversationResponseDTOS::add);

		conversationResponseDTOS.sort((c1, c2) -> {
			LocalDateTime lastMessageC1 = c1.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);
			LocalDateTime lastMessageC2 = c2.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);

			return lastMessageC2.compareTo(lastMessageC1);
		});

		return conversationResponseDTOS;
	}

	public static ConversationResponseDTO buildMessages(Long userId, ConversationModel conv,
			Map<Long, List<MessageModel>> messageByConversation, Map<Long, Long> totalMessagesByConversation) 
	{

		ConversationResponseDTO dto = new ConversationResponseDTO();
		dto.setId(conv.getId());

		Map<Long, TranslationConfigResponseDTO> configsUser = new HashMap<>();
		for (UserModel participant : conv.getParticipants()) {
			configsUser.put(participant.getId(), buildTranslationConfig(participant.getId(), conv));
		}

		dto.setConfigsUser(configsUser);
		
		dto.setType(conv.getType());
		dto.setProfilePic(getProfilePic(conv, userId));
		dto.setUserInfo(buildInfo(conv, userId));
		dto.setTotalMessages(totalMessagesByConversation.getOrDefault(conv.getId(), 0L).intValue());
		if(StringUtils.isNotBlank(conv.getConversationName())) {
			dto.setConversationName(conv.getConversationName());
		} else {
			dto.setConversationName(getConversationName(conv, userId));
		}

		List<MessageRecord> messages = getDecryptedMessages(conv, messageByConversation);

		dto.setMessages(messages);
		dto.setLastMessage(determineLastMessage(messages, messages));

		return dto;
	}

	public static UserDTO buildInfo(ConversationModel conversationModel, Long userId) {
		return conversationModel.getParticipants().stream().filter(participant -> !participant.getId().equals(userId))
				.map(UserDTO::new).findFirst().orElse(null);
	}

	public static String getProfilePic(ConversationModel conversationModel, Long userId) {
		UserModel user = conversationModel.getParticipants().stream()
				.filter(participant -> !participant.getId().equals(userId)).findFirst().orElse(null);

		if (user != null && user.getUserProfile() != null && user.getUserProfile().getContent() != null) {
			return convertByteArrayToBase64(user.getUserProfile().getContent());
		}

		return "";
	}

	public static String getConversationName(ConversationModel conv, Long userId) {
		List<String> participantNames = conv.getParticipants().stream()
				.filter(participant -> !participant.getId().equals(userId)).map(UserModel::getName).toList();

		return String.join(", ", participantNames);
	}

	public static String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	public static List<MessageRecord> getDecryptedMessages(ConversationModel conv, Map<Long, List<MessageModel>> messageByConversation) {
		List<MessageModel> messages = messageByConversation.getOrDefault(conv.getId(), new ArrayList<>());
		List<MessageRecord> decryptedMessages = new ArrayList<>();

		for (MessageModel message : messages) {
			decryptedMessages.add(decryptMessage(message));
		}

		return decryptedMessages;
	}

	public static MessageRecord decryptMessage(MessageModel msg) {
	    try {
	        String decryptedContent = decryptContent(msg.getContent());
	        String decryptedOriginalContent = decryptOriginalContent(msg.getContentOriginal());
	        MessageContent messageContent = new MessageContent(decryptedContent, decryptedOriginalContent);
	        String image = encodeImageToBase64(msg);

	        return buildMessageRecord(msg, decryptedContent, decryptedOriginalContent, messageContent, image);
	    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException 
	             | IllegalBlockSizeException | BadPaddingException e) {
	        throw new ConvervationMessageBuildException(e);
	    }
	}

	private static String decryptContent(String content) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return Encrypt.decrypt(content);
	}

	private static String decryptOriginalContent(String contentOriginal) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return StringUtils.isNotBlank(contentOriginal) ? Encrypt.decrypt(contentOriginal) : "";
	}

	private static String encodeImageToBase64(MessageModel msg) {
		return Optional.ofNullable(msg.getImage()).map(image -> image.getContent())
				.map(content -> Base64.getEncoder().encodeToString(content)).orElse("");
	}

	private static MessageRecord buildMessageRecord(MessageModel msg, String decryptedContent,
			String decryptedOriginalContent, MessageContent messageContent, String image) {
		return MessageRecord.builder().id(msg.getId()).content(decryptedContent).sender(msg.getSender().getUsername())
				.timeStamp(msg.getSentAt()).senderId(msg.getSender().getId())
				.isEdited(Optional.ofNullable(msg.getIsEdited()).orElse(false))
				.viewed(Optional.ofNullable(msg.getViewed()).orElse(false))
				.viewedTime(Optional.ofNullable(msg.getViewedAt()).orElse(null))
				.isDeleted(Optional.ofNullable(msg.getIsDeleted()).orElse(false)).messageContent(messageContent)
				.originalContent(decryptedOriginalContent).image(image).build();
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
				.filter(config -> config.getUser().getId().compareTo(userId) == 0 && config.getType().equals(type))
				.map(trans -> trans.getTargetLanguage()).findFirst().orElse("");
	}

}
