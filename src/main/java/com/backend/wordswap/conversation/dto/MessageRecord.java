package com.backend.wordswap.conversation.dto;

import java.time.LocalDateTime;

public record MessageRecord(Long id, String content, String sender, LocalDateTime timeStamp, Long senderId,
		boolean isEdited, boolean isDeleted) {
}
