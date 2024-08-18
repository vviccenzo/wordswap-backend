package com.backend.wordswap.friendship.dto;

import java.time.LocalDateTime;

import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;

public record FriendshipDTO(Long id, String sender, String receiver, StatusType status, LocalDateTime creationDate) {

}
