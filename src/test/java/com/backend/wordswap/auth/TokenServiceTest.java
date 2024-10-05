package com.backend.wordswap.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.auth.token.TokenService;
import com.backend.wordswap.user.entity.UserModel;

class TokenServiceTest {

	@InjectMocks
	private TokenService tokenService;

	private String secret = "aaaaaaaaaa"; // Define diretamente o valor do secret

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		tokenService.setSecret(secret); // Método setter para definir o secret
	}

	@Test
	void testGenerateToken() {
		UserModel user = new UserModel();
		user.setUsername("testUser");

		String token = tokenService.generateToken(user);

		assertNotNull(token);
		assertTrue(token.startsWith("ey"));
	}

	@Test
	void testValidateToken() {
		UserModel user = new UserModel();
		user.setUsername("testUser");
		String token = tokenService.generateToken(user);

		String subject = tokenService.validateToken(token);

		assertEquals(user.getUsername(), subject);
	}

	@Test
	void testValidateInvalidToken() {
		String invalidToken = "invalidToken";

		String subject = tokenService.validateToken(invalidToken);

		assertEquals("", subject);
	}
}
