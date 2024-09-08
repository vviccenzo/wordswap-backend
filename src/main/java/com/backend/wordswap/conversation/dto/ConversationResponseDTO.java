package com.backend.wordswap.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDTO {

	private Long id;

	private Long senderId;

	private Long receiverId;

	private String conversationName;

	private String profilePic;

	private int totalMessages;

	private Map<Long, TranslationConfigResponseDTO> configsUser;

	private Map<LocalDateTime, String> lastMessage;

	private List<MessageRecord> userMessages;

	private List<MessageRecord> targetUserMessages;

}
