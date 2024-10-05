package com.backend.wordswap.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.auth.dto.AuthDTO;
import com.backend.wordswap.auth.login.LoginService;
import com.backend.wordswap.auth.token.TokenService;
import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.InvalidCredentialsException;
import com.backend.wordswap.user.exception.UserNotFoundException;

class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private LoginService loginService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLogin_UserNotFoundException() {
		String username = "nonexistentUser";
		String password = "password";

		when(this.userRepository.findByUsername(username)).thenReturn(Optional.empty());

		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
			loginService.login(username, password);
		});

		assertEquals("User not found.", exception.getMessage());
	}

	@Test
	void testLogin_InvalidCredentialsException() {
		String username = "existingUser";
		String password = "wrongPassword";

		UserModel userModel = new UserModel();
		userModel.setUsername(username);
		userModel.setPassword(BCryptUtil.encryptPassword("correctPassword"));

		when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(userModel));

		InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
			loginService.login(username, password);
		});

		assertEquals("Invalid credentials.", exception.getMessage());
	}

	@Test
	void testLogin_Success() {
		String username = "existingUser";
		String password = "correctPassword";

		UserModel userModel = new UserModel();
		userModel.setUsername(username);
		userModel.setPassword(BCryptUtil.encryptPassword(password));

		when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(userModel));
		when(this.tokenService.generateToken(userModel)).thenReturn("mockedToken");

		AuthDTO token = this.loginService.login(username, password);

		assertEquals("mockedToken", token.token());
	}

}
