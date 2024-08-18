package com.backend.wordswap.message.dto;

import lombok.Data;

@Data
public class MessageCreateDTO {

    private Long senderId;

    private Long receiverId;

    private Long conversationId = 0L;

    private String content;

    private Boolean isTranslation = Boolean.FALSE;

    private String targetLanguage;

}
