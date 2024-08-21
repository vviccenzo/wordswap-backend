package com.backend.wordswap.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDTO {

	private Long id;

	private Long senderId;

	private String conversationName;

	private byte[] profilePic;

	private Map<LocalDateTime, String> lastMessage;

	private List<MessageRecord> userMessages;

	private List<MessageRecord> targetUserMessages;

}
