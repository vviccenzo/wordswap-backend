package com.backend.wordswap.user.dto;

public record UserInfoDTO(Long id, byte[] profilePic, String name, String bio) {
}
