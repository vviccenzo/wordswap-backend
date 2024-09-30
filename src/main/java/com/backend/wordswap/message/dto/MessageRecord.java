package com.backend.wordswap.message.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageRecord {

	private Long id;

	private String content;

	private String sender;

	private LocalDateTime timeStamp;

	private Long senderId;

	private boolean isEdited;

	private boolean isDeleted;

	@JsonInclude(Include.NON_NULL)
	private MessageContent messageContent;

}
