package com.backend.wordswap.user.dto;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Objects;

import com.backend.wordswap.user.entity.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;

	private Long conversationId;

	private String bio;

	private String label;

	private String profilePic;
	
	private String userCode;

	private LocalDate createdDate;

	public UserDTO(UserModel user) {
		this.id = user.getId();
		this.label = user.getName();
		this.profilePic = this.getProfilePic(user);
		this.bio = user.getBio();
	}

	private String getProfilePic(UserModel user) {
		boolean isValid = Objects.nonNull(user.getUserProfile()) && Objects.nonNull(user.getUserProfile().getContent());
		if (isValid) {
			this.convertByteArrayToBase64(user.getUserProfile().getContent());
		}

		return "";
	}

	public String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
