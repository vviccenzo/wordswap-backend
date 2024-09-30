package com.backend.wordswap.user.dto;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Objects;

import com.backend.wordswap.user.entity.UserModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;

	@JsonInclude(Include.NON_NULL)
	private Long conversationId;

	@JsonInclude(Include.NON_NULL)
	private String bio;

	@JsonInclude(Include.NON_NULL)
	private String label;

	@JsonInclude(Include.NON_NULL)
	private String profilePic;
	
	@JsonInclude(Include.NON_NULL)
	private String userCode;

	@JsonInclude(Include.NON_NULL)
	private LocalDate createdDate;

	public UserDTO(UserModel user) {
		this.id = user.getId();
		this.label = user.getName();
		this.profilePic = this.getProfilePic(user);
		this.bio = user.getBio();
		this.userCode = user.getUserCode();
		this.createdDate = user.getCreationDate();
	}

	private String getProfilePic(UserModel user) {
		boolean isValid = Objects.nonNull(user.getUserProfile()) && Objects.nonNull(user.getUserProfile().getContent());
		if (isValid) {
			return this.convertByteArrayToBase64(user.getUserProfile().getContent());
		}

		return "";
	}

	public String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
