package com.backend.wordswap.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversartionArchiveDTO {

	private Long id;

	private Long userId;

	private Boolean hasToArchive;

}
