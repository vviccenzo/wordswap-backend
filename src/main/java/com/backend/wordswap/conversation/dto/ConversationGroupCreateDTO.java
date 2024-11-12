package com.backend.wordswap.conversation.dto;

import java.util.Set;

import lombok.Data;

@Data
public class ConversationGroupCreateDTO {

	private Set<Long> userIds;

	private String name;

	private String bio;

	private String imageContent;

	private String imageFileName;

}
