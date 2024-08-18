package com.backend.wordswap.friendship.request;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FriendshipRequestFactory {

	public static FriendshipDTO buildDTO(FriendshipRequestModel model) {
		return new FriendshipDTO(model.getId(), model.getSender().getUsername(), model.getReceiver().getUsername(),
				model.getStatus(), model.getRequestDate());
	}

}
