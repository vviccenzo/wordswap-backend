package com.backend.wordswap.conversation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.backend.wordswap.conversation.entity.ConversationType;
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

	private Boolean isArchivedInitiator;
	private Boolean isArchivedRecipient;

	private UserDTO userInfo;

	private int totalMessages;

	private List<MessageRecord> messages;
	private Map<Long, TranslationConfigResponseDTO> configsUser;
	private Map<LocalDateTime, String> lastMessage;

	private ConversationType type;
}
