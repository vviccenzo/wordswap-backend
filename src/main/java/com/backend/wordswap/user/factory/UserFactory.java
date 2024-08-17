package com.backend.wordswap.user.factory;

import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.entity.UserRole;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class UserFactory {

	public static UserModel createModelFromDto(UserCreateDTO dto) throws IOException {
		UserModel model = new UserModel();
		model.setUsername(dto.getUsername());
		model.setEmail(dto.getEmail());
		model.setPassword(BCryptUtil.encryptPassword(dto.getPassword()));
		model.setCreationDate(LocalDate.now());
		model.setRole(UserRole.USER);

		if (dto.getFile() != null && dto.getFile().getBytes().length > 0) {
			UserProfileModel profilePic = new UserProfileModel();
			profilePic.setContent(dto.getFile().getBytes());
			profilePic.setFileName(dto.getFile().getName());
			profilePic.setUpdateDate(LocalDate.now());
			profilePic.setUser(model);

			model.setUserProfile(profilePic);
		}

		return model;
	}

	public static UserModel createModelFromDto(UserUpdateDTO dto, UserModel model) {
		model.setUsername(dto.getUsername());
		model.setPassword(dto.getPassword());

		return model;
	}

	public static List<UserDTO> buildList(List<UserModel> friends) {
		return friends.stream().map(model -> new UserDTO(model.getId(), model.getUsername(), model.getCreationDate()))
				.toList();
	}
}
