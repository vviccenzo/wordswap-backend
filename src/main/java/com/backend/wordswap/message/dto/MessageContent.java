package com.backend.wordswap.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageContent {

	@JsonInclude(Include.NON_NULL)
	private String content;

	@JsonInclude(Include.NON_NULL)
	private String contentReceiving;

}
