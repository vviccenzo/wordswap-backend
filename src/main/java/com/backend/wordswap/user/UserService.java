package com.backend.wordswap.user;

import com.backend.wordswap.user.dto.*;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.factory.UserFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<UserResponseDTO> findAll() {
		return this.userRepository
				.findAll().stream().map(
						userModel -> new UserResponseDTO(userModel.getId(), userModel.getUsername()))
				.toList();
	}

	@Transactional
	public UserDTO save(UserCreateDTO dto) {
		UserModel model = this.userRepository.save(new UserFactory().createModelFromDto(dto));
		model.setUserCode(model.getUsername() + "_" + model.getId());

		model = this.userRepository.save(model);

		return new UserDTO(model.getId(), model.getUsername(), model.getCreationDate());
	}

	@Transactional
	public UserDTO update(UserUpdateDTO dto) {
		Optional<UserModel> model = this.userRepository.findById(dto.getId());
		if (model.isPresent()) {
			UserModel modelToUpdate = new UserFactory().createModelFromDto(dto, model.get());
			return new UserDTO(modelToUpdate.getId(), modelToUpdate.getUsername(), modelToUpdate.getCreationDate());
		}

		throw new RuntimeException("User not founded.");
	}

	@Transactional
	public void delete(Long id) {
		this.userRepository.deleteById(id);
	}

}
