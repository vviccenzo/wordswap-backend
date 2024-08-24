package com.backend.wordswap.user.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserEditDTO {

	private Long id;

	private String name;

	private String bio;

	private MultipartFile file;

}
