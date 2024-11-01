package com.backend.wordswap.friendshipRequest;


import com.backend.wordswap.friendshipRequest.dto.FriendshipDTO;
import com.backend.wordswap.friendshipRequest.entity.FriendshipRequestModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FriendshipRequestFactory {

	public static FriendshipDTO buildDTO(FriendshipRequestModel model) {
		return new FriendshipDTO(model.getId(), model.getSender().getUsername(), model.getReceiver().getUsername(),
				model.getStatus(), model.getRequestDate());
	}

}
