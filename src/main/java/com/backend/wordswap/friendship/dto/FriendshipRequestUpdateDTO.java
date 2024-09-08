package com.backend.wordswap.friendship.dto;

import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;

public record FriendshipRequestUpdateDTO(Long inviteId, StatusType statusType) {

}
