package com.backend.wordswap.user.dto;

import java.time.LocalDate;
import java.util.Base64;

import com.backend.wordswap.user.entity.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;

	private String label;

	private LocalDate createdDate;

	private Long conversationId;

	private String profilePic;

	private String bio;

	public UserDTO(UserModel user) {
		this.id = user.getId();
		this.label = user.getName();
		this.profilePic = this.getProfilePic(user);
		this.bio = user.getBio();
	}

	private String getProfilePic(UserModel user) {
		return this.convertByteArrayToBase64(user.getUserProfile().getContent());
	}

	public String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
