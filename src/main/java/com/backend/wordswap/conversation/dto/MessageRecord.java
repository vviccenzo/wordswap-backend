package com.backend.wordswap.conversation.dto;

import java.time.LocalDateTime;

public record MessageRecord(Long id, String text, String sender, LocalDateTime timestamp) {
}
