package com.backend.wordswap.user;

import com.backend.wordswap.user.dto.*;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;
import com.backend.wordswap.user.factory.UserFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserResponseDTO> findAll() {
		return userRepository.findAll().stream()
				.map(userModel -> new UserResponseDTO(userModel.getId(), userModel.getUsername())).toList();
	}

	@Transactional
	public UserDTO save(UserCreateDTO dto) throws IOException {
		this.validateUser(dto);

		UserModel model = UserFactory.createModelFromDto(dto);
		model = this.userRepository.save(model);

		model.setUserCode(model.getUsername() + "_" + model.getId());

		UserModel savedModel = this.userRepository.save(model);
		return new UserDTO(savedModel.getId(), savedModel.getUsername(), savedModel.getCreationDate());
	}

	@Transactional
	public UserDTO update(UserUpdateDTO dto) {
		UserModel modelToUpdate = this.userRepository.findById(dto.getId())
				.orElseThrow(() -> new UserNotFoundException("User not found."));

		this.validateUser(dto);

		UserModel updatedModel = UserFactory.createModelFromDto(dto, modelToUpdate);
		UserModel savedModel = this.userRepository.save(updatedModel);

		return new UserDTO(savedModel.getId(), savedModel.getUsername(), savedModel.getCreationDate());
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

	private void validateUser(UserUpdateDTO dto) {
		if (this.userRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new UsernameAlreadyExistsException("User with this username already exists.");
		}
	}
}