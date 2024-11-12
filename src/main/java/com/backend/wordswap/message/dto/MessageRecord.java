package com.backend.wordswap.message.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecord {

	private Long id;

	private Long senderId;

	private String content;

	private String sender;

	private String image;

	private String originalContent;

	@JsonInclude(Include.NON_EMPTY)
	private LocalDateTime timeStamp;

	@JsonInclude(Include.NON_EMPTY)
	private LocalDateTime viewedTime;

	private boolean isEdited;

	private boolean isDeleted;
	
	private boolean viewed;

	@JsonInclude(Include.NON_NULL)
	private MessageContent messageContent;

}
