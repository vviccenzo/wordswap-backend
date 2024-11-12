package com.backend.wordswap.message.dto;

import java.util.Set;

import lombok.Data;

@Data
public class MessageViewDTO {

	private Set<Long> messageIds;

}
