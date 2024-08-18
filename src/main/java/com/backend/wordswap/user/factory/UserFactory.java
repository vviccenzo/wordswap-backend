package com.backend.wordswap.user.factory;

import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.conversation.entity.ConversationModel;
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

import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class UserFactory {

	public static UserModel createModelFromDto(UserCreateDTO dto) throws IOException {
		UserModel model = new UserModel();
		populateUserModelFromCreateDTO(dto, model);
		if (dto.getFile() != null && dto.getFile().getBytes().length > 0) {
			model.setUserProfile(createUserProfile(dto.getFile(), model));
		}
		return model;
	}

	public static UserModel createModelFromDto(UserUpdateDTO dto, UserModel model) {
		populateUserModelFromUpdateDTO(dto, model);
		return model;
	}

	public static List<UserDTO> buildList(List<UserModel> users, Long currentUserId) {
		return users.stream().map(model -> {
	        Long conversationId = model.getInitiatedConversations().stream()
	                .filter(conversation -> conversation.getUserRecipient().getId().equals(currentUserId))
	                .map(ConversationModel::getId)
	                .findFirst()
	                .orElseGet(() -> model.getReceivedConversations().stream()
	                    .filter(conversation -> conversation.getUserInitiator().getId().equals(currentUserId))
	                    .map(ConversationModel::getId)
	                    .findFirst()
	                    .orElse(null)
	                );
			
			return new UserDTO(model.getId(), model.getUsername(), model.getCreationDate(), conversationId);
		}).toList();
	}

	private static void populateUserModelFromCreateDTO(UserCreateDTO dto, UserModel model) {
		model.setUsername(dto.getUsername());
		model.setEmail(dto.getEmail());
		model.setPassword(BCryptUtil.encryptPassword(dto.getPassword()));
		model.setCreationDate(LocalDate.now());
		model.setRole(UserRole.USER);
	}

	private static void populateUserModelFromUpdateDTO(UserUpdateDTO dto, UserModel model) {
		model.setUsername(dto.getUsername());
		model.setPassword(dto.getPassword());
	}

	private static UserProfileModel createUserProfile(MultipartFile file, UserModel user) throws IOException {
		UserProfileModel profile = new UserProfileModel();
		profile.setContent(file.getBytes());
		profile.setFileName(file.getName());
		profile.setUpdateDate(LocalDate.now());
		profile.setUser(user);
		return profile;
	}
}
