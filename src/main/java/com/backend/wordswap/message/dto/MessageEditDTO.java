package com.backend.wordswap.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEditDTO {

	private Long id;

	private Long senderId;

	private String content;

	private int pageNumber;

}
