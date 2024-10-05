package com.backend.wordswap.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;

class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	void testSaveUser() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "filename.txt", MediaType.TEXT_PLAIN_VALUE,
				"content".getBytes());
		UserDTO userDTO = new UserDTO();
		userDTO.setLabel("testUser");

		when(this.userService.save(any(UserCreateDTO.class))).thenReturn(userDTO);

		mockMvc.perform(multipart("/user").file(file).param("username", "testUser").param("password", "password")
				.param("email", "test@example.com").param("name", "Test User")
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
				.andExpect(jsonPath("$.label").value("testUser"));

		verify(this.userService).save(any(UserCreateDTO.class));
	}

	@Test
	void testUpdateUser() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setLabel("Updated User");

		when(this.userService.update(any(UserUpdateDTO.class))).thenReturn(userDTO);
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE,
				"content".getBytes());

		mockMvc.perform(multipart("/user").file(mockFile).param("id", "1").param("name", "Updated User")
				.param("bio", "Updated bio").contentType(MediaType.MULTIPART_FORM_DATA).with(request -> {
					request.setMethod("PUT");
					return request;
				})).andExpect(status().isOk()).andExpect(jsonPath("$.label").value("Updated User"));

		verify(this.userService).update(any(UserUpdateDTO.class));
	}

	@Test
	void testDeleteUser() throws Exception {
		Long userId = 1L;

		mockMvc.perform(delete("/user").param("id", userId.toString())).andExpect(status().isOk());

		verify(this.userService).delete(userId);
	}

	@Test
	void testFindFriends() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO();
		userDTO.setLabel("friendUser");

		when(this.userService.findFriendsByUserId(userId)).thenReturn(Collections.singletonList(userDTO));

		mockMvc.perform(get("/user/find-friends").param("userId", userId.toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].label").value("friendUser"));

		verify(this.userService).findFriendsByUserId(userId);
	}
}
