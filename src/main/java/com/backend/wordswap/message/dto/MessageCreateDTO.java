package com.backend.wordswap.message.dto;

import java.util.Base64;

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

	private int pageNumber;
	
	private String conversationCode;

	private String content;

	private String imageContent;

	private String imageFileName;

	public MessageCreateDTO(Long senderId, String content, Long receiverId) {
		this.senderId = senderId;
		this.content = content;
		this.receiverId = receiverId;
	}

    public byte[] getImageBytes() {
        return Base64.getDecoder().decode(imageContent);
    }

}
