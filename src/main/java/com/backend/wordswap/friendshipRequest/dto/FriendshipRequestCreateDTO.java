package com.backend.wordswap.friendshipRequest.dto;

import lombok.Data;

@Data
public class FriendshipRequestCreateDTO {

	private Long senderId;

	private String targetUserCode;

}
