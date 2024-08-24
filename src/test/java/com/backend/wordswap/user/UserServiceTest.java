package com.backend.wordswap.user;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;
import com.backend.wordswap.user.factory.UserFactory;

import java.io.IOException;
import java.util.Optional;

class UserServiceTest {

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
		UserCreateDTO dto = new UserCreateDTO("username", "email@example.com", "password", null);
		UserModel userModel = UserFactory.createModelFromDto(dto);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(this.userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
		when(this.userRepository.save(any(UserModel.class))).thenReturn(userModel);

		UserDTO result = this.userService.save(dto);

		assertNotNull(result);
		assertEquals(dto.getUsername(), result.getLabel());
	}

	@Test
	void save_ShouldThrowException_WhenEmailExists() throws IOException {
		UserCreateDTO dto = new UserCreateDTO("username", "email@example.com", "password", null);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new UserModel()));

		UserEmailAlreadyExistsException thrown = assertThrows(UserEmailAlreadyExistsException.class,
				() -> this.userService.save(dto));
		assertEquals("User with this email already exists.", thrown.getMessage());
	}

	@Test
	void save_ShouldThrowException_WhenUsernameExists() throws IOException {
		UserCreateDTO dto = new UserCreateDTO("username", "email@example.com", "password", null);

		when(this.userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(this.userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new UserModel()));

		UsernameAlreadyExistsException thrown = assertThrows(UsernameAlreadyExistsException.class,
				() -> this.userService.save(dto));
		assertEquals("User with this username already exists.", thrown.getMessage());
	}
}
