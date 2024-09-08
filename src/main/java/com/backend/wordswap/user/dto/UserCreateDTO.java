package com.backend.wordswap.user.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

	private String name;

	private String email;

	private String username;

	private String password;

	private MultipartFile file;

}
