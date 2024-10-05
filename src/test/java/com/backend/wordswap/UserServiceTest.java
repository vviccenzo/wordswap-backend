package com.backend.wordswap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.UserService;
import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;
import com.backend.wordswap.user.factory.UserFactory;

import java.io.IOException;
import java.util.Optional;

class UserServiceTest extends WordswapApplicationTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void save_ShouldSaveUser_WhenUserDoesNotExist() throws IOException {
		UserCreateDTO dto = new UserCreateDTO("name", "username", "email@example.com", "password", null);
		UserModel userModel = UserFactory.createModelFromDto(dto);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(this.userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
		when(this.userRepository.save(any(UserModel.class))).thenReturn(userModel);

		UserDTO result = this.userService.save(dto);

		assertNull(result.getId());
	}

	@Test
	void save_ShouldThrowException_WhenEmailExists() throws IOException {
		UserCreateDTO dto = new UserCreateDTO("name", "username", "email@example.com", "password", null);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new UserModel()));

		UserEmailAlreadyExistsException thrown = assertThrows(UserEmailAlreadyExistsException.class,
				() -> this.userService.save(dto));
		assertEquals("User with this email already exists.", thrown.getMessage());
	}

	@Test
	void save_ShouldThrowException_WhenUsernameExists() throws IOException {
		UserCreateDTO dto = new UserCreateDTO("name", "username", "email@example.com", "password", null);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(this.userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new UserModel()));

		UsernameAlreadyExistsException thrown = assertThrows(UsernameAlreadyExistsException.class,
				() -> this.userService.save(dto));
		assertEquals("User with this username already exists.", thrown.getMessage());
	}

	@Test
	void update_ShouldUpdateUser_WhenUserExists() throws IOException {
		Long userId = 1L;
		String name = "Updated Name";
		String bio = "Updated Bio";
		MultipartFile file = mock(MultipartFile.class);
		byte[] fileContent = "dummyContent".getBytes();

		when(file.getBytes()).thenReturn(fileContent);

		UserUpdateDTO updateDTO = new UserUpdateDTO(userId, name, bio, file);
		UserModel existingUser = new UserModel();
		existingUser.setId(userId);
		
		UserModel userEdited = UserFactory.updateModelFromDto(updateDTO, existingUser);

		when(this.userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(this.userRepository.save(existingUser)).thenReturn(userEdited);

		UserDTO result = this.userService.update(updateDTO);

		assertNotNull(result);
		assertEquals(name, result.getLabel());
		assertEquals(bio, result.getBio());

		verify(this.userRepository).findById(userId);
		verify(this.userRepository).save(existingUser);
	}

	@Test
	void update_ShouldThrowException_WhenUserDoesNotExist() {
		Long userId = 1L;
		UserUpdateDTO updateDTO = new UserUpdateDTO(userId, "Name", "Bio", null);

		when(this.userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> this.userService.update(updateDTO));

		verify(this.userRepository).findById(userId);
		verify(this.userRepository, never()).save(any(UserModel.class));
	}
}
