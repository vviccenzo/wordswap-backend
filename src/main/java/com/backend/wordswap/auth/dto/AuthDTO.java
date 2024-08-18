package com.backend.wordswap.auth.dto;

import com.backend.wordswap.user.dto.UserInfoDTO;

public record AuthDTO(String token, UserInfoDTO userInfo) {

}
