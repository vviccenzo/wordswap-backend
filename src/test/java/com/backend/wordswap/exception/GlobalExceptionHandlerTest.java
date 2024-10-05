package com.backend.wordswap.exception;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.backend.wordswap.domain.GlobalExceptionHandler;

@WebMvcTest(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

//	@Autowired
//	private MockMvc mockMvc;
//
//	private GlobalExceptionHandler globalExceptionHandler;
//
//	@BeforeEach
//	void setUp() {
//		globalExceptionHandler = new GlobalExceptionHandler();
//		mockMvc = MockMvcBuilders.standaloneSetup(globalExceptionHandler).build();
//	}
//
//	@Test
//	void handleFriendshipAlreadySendedException() throws Exception {
//		String message = "Friendship request already sent";
//		mockMvc.perform(get("/auth/friendship-already-sended") // Simulação de endpoint
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andExpect(content().string(message));
//	}
//
//	@Test
//	void handleUserNotFoundException() throws Exception {
//		String message = "User not found";
//		mockMvc.perform(get("/auth/user-not-found") // Simulação de endpoint
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andExpect(content().string(message));
//	}
//
//	@Test
//	void handleUsernameAlreadyExistsException() throws Exception {
//		String message = "Username already exists";
//		mockMvc.perform(get("/auth/username-already-exists") // Simulação de endpoint
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andExpect(content().string(message));
//	}
//
//	@Test
//	void handleUserEmailAlreadyExistsException() throws Exception {
//		String message = "User email already exists";
//		mockMvc.perform(get("/auth/user-email-already-exists") // Simulação de endpoint
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andExpect(content().string(message));
//	}
}
