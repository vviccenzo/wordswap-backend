package com.backend.wordswap.conversation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.backend.wordswap.message.dto.MessageRecord;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.user.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDTO {

	private Long id;

	private Long senderId;

	private Long receiverId;

	private String senderCode;

	private String receiverCode;

	private String conversationName;

	private String profilePic;

	private UserDTO userInfo;

	private int totalMessages;

	private Map<Long, TranslationConfigResponseDTO> configsUser;

	private Map<LocalDateTime, String> lastMessage;

	private List<MessageRecord> userMessages;

	private List<MessageRecord> targetUserMessages;

}
