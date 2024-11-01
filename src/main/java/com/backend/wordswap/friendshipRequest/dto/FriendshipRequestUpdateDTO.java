package com.backend.wordswap.friendshipRequest.dto;

import com.backend.wordswap.friendshipRequest.entity.enumeration.StatusType;

public record FriendshipRequestUpdateDTO(Long inviteId, StatusType statusType) {

}
