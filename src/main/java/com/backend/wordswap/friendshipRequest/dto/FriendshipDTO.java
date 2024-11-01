package com.backend.wordswap.friendshipRequest.dto;

import java.time.LocalDateTime;

import com.backend.wordswap.friendshipRequest.entity.enumeration.StatusType;

public record FriendshipDTO(Long id, String sender, String receiver, StatusType status, LocalDateTime creationDate) {

}
