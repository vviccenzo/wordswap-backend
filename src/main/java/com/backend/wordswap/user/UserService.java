package com.backend.wordswap.user;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserResponseDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;
import com.backend.wordswap.user.factory.UserFactory;
import com.backend.wordswap.user.profile.UserProfileRepository;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final UserProfileRepository userProfileRepository;

	public List<UserResponseDTO> findAll() {
		return userRepository.findAll().stream()
				.map(userModel -> new UserResponseDTO(userModel.getId(), userModel.getUsername())).toList();
	}

	@Transactional(rollbackOn = Exception.class)
	public UserDTO save(UserCreateDTO dto) throws IOException {
	    this.validateUser(dto);

	    UserModel userModel = UserFactory.createModelFromDto(dto);

	    String uniqueSuffix = UUID.randomUUID().toString();
	    userModel.setUserCode(userModel.getUsername() + "_" + uniqueSuffix);

	    UserModel savedUser = this.userRepository.save(userModel);

	    if (dto.getFile() != null) {
	        UserProfileModel profilePic = UserFactory.createUserProfile(dto.getFile(), savedUser);
	        this.userProfileRepository.save(profilePic);
	        savedUser.setUserProfile(profilePic);
	    }

	    savedUser.setName(dto.getName());

	    return new UserDTO(savedUser);
	}

	@Transactional
	public UserDTO update(UserUpdateDTO dto) throws IOException {
		UserModel modelToUpdate = this.userRepository.findById(dto.getId()).orElseThrow(() -> new UserNotFoundException("User not found."));
		UserModel updatedModel = UserFactory.updateModelFromDto(dto, modelToUpdate);
		UserModel savedModel = this.userRepository.save(updatedModel);

	    if (dto.getFile() != null) {
			UserProfileModel profilePic = UserFactory.createUserProfile(dto.getFile(), updatedModel);
			profilePic = this.userProfileRepository.save(profilePic); // Salva o perfil e retorna a versão persistida
			updatedModel.setUserProfile(profilePic);
	    }

		savedModel.setName(dto.getName());

		return new UserDTO(savedModel);
	}

	@Transactional
	public void delete(Long id) {
		if (!this.userRepository.existsById(id)) {
			throw new UserNotFoundException("User not found.");
		}

		this.userRepository.deleteById(id);
	}

	private void validateUser(UserCreateDTO dto) {
		if (this.userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new UserEmailAlreadyExistsException("User with this email already exists.");
		}

		if (this.userRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new UsernameAlreadyExistsException("User with this username already exists.");
		}
	}

	public List<UserDTO> findFriendsByUserId(Long userId) {
		Optional<UserModel> optModel = this.userRepository.findById(userId);
		if (optModel.isPresent()) {
			return UserFactory.buildList(optModel.get().getFriends(), userId);
		}

		throw new UserNotFoundException("User not founded.");
	}

}
