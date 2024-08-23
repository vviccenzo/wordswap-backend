package com.backend.wordswap.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageEditDTO {

	private Long id;

	private String content;

}
