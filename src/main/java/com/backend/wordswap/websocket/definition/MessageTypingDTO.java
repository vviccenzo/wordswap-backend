package com.backend.wordswap.websocket.definition;

import lombok.Data;

@Data
public class MessageTypingDTO {

	private Boolean isTyping;

	private Long userTyping;

	private Long conversationId;

}
