package com.backend.wordswap.user.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateDTO {

	private Long id;

	private String name;

	private String bio;
	
	private MultipartFile file;

}
