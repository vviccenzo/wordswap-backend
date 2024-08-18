package com.backend.wordswap.friendship.request.dto;

import lombok.Data;

@Data
public class FriendshipRequestCreateDTO {

	private Long senderId;

	private String targetUserCode;

}
