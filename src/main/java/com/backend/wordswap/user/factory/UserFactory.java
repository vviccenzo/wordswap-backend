package com.backend.wordswap.user.factory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.entity.UserRole;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFactory {

	public static UserModel createModelFromDto(UserCreateDTO dto) throws IOException {
		UserModel model = new UserModel();
		populateUserModel(dto, model);

		return model;
	}

	public static UserModel updateModelFromDto(UserUpdateDTO dto, UserModel model) {

		try {
			populateUserModel(dto, model);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return model;
	}

	public static List<UserDTO> buildList(List<UserModel> users, Long currentUserId) {
		return users.stream().map(model -> new UserDTO(model.getId(), model.getUsername(), model.getCreationDate(),
				findConversationId(model, currentUserId), getProfilePic(model), getBio(model))).toList();
	}

	private static Long findConversationId(UserModel model, Long currentUserId) {
		return model.getInitiatedConversations().stream()
				.filter(conversation -> conversation.getUserRecipient().getId().equals(currentUserId))
				.map(ConversationModel::getId).findFirst()
				.orElseGet(() -> model.getReceivedConversations().stream()
						.filter(conversation -> conversation.getUserInitiator().getId().equals(currentUserId))
						.map(ConversationModel::getId).findFirst().orElse(null));
	}

	private static String getBio(UserModel user) {
		return Optional.ofNullable(user.getBio()).orElse("");
	}

	private static String getProfilePic(UserModel user) {
		return Optional.ofNullable(user.getUserProfile()).map(profile -> convertByteArrayToBase64(profile.getContent()))
				.orElse("");
	}

	private static String convertByteArrayToBase64(byte[] imageBytes) {
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	private static void populateUserModel(Object dto, UserModel model) throws IOException {
		if (dto instanceof UserCreateDTO userCreateDTO) {
			model.setUsername(userCreateDTO.getUsername());
			model.setEmail(userCreateDTO.getEmail());
			model.setPassword(BCryptUtil.encryptPassword(userCreateDTO.getPassword()));
			model.setCreationDate(LocalDate.now());
			model.setRole(UserRole.USER);
			model.setName(userCreateDTO.getName());
			handleProfilePic(userCreateDTO.getFile(), model);
		} else if (dto instanceof UserUpdateDTO userUpdateDTO) {
			model.setName(userUpdateDTO.getName());
			model.setBio(userUpdateDTO.getBio());
			handleProfilePic(userUpdateDTO.getFile(), model);
		}
	}

	private static void handleProfilePic(MultipartFile file, UserModel model) throws IOException {
		if (file != null && !file.isEmpty()) {
			if (model.getUserProfile() != null) {
				model.getUserProfile().setContent(file.getBytes());
			} else {
				model.setUserProfile(createUserProfile(file, model));
			}
		}
	}

	private static UserProfileModel createUserProfile(MultipartFile file, UserModel user) throws IOException {
		UserProfileModel profile = new UserProfileModel();
		profile.setContent(file.getBytes());
		profile.setFileName(file.getOriginalFilename());
		profile.setUpdateDate(LocalDate.now());
		profile.setUser(user);

		return profile;
	}
}
