package com.backend.wordswap.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

	private Long id;

	private String username;

	private String password;

}
