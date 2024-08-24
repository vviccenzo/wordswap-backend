package com.backend.wordswap.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateDTO {

    private Long senderId;

    private Long receiverId;

    private Long conversationId = 0L;

    private String content;

    private Boolean isTranslation = Boolean.FALSE;

    private String targetLanguage;

    public MessageCreateDTO(Long senderId, String content, Long receiverId) {
    	this.senderId = senderId;
    	this.content = content;
    	this.receiverId = receiverId;
    }
}
