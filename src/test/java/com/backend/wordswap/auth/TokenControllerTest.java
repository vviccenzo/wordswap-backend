package com.backend.wordswap.auth;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.backend.wordswap.auth.token.TokenController;
import com.backend.wordswap.auth.token.TokenService;

class TokenControllerTest {

	private MockMvc mockMvc;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private TokenController tokenController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(tokenController).build();
	}

	@Test
	void testIsTokenValid_WhenTokenIsValid() throws Exception {
		String validToken = "validToken";

		when(this.tokenService.validateToken(validToken)).thenReturn("someValidToken");

		mockMvc.perform(
				get("/auth/token/validate-token").param("token", validToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.isValid").value(true));

		verify(this.tokenService, times(1)).validateToken(validToken);
	}

	@Test
	void testIsTokenValid_WhenTokenIsInvalid() throws Exception {
		String invalidToken = "invalidToken";

		when(this.tokenService.validateToken(invalidToken)).thenReturn("");

		mockMvc.perform(
				get("/auth/token/validate-token").param("token", invalidToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.isValid").value(false));

		verify(this.tokenService, times(1)).validateToken(invalidToken);
	}
}
