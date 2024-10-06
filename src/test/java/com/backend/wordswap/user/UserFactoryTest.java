package com.backend.wordswap.user;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserInfoDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.factory.UserFactory;

class UserFactoryTest {

	@Mock
	private MultipartFile mockFile;

	private UserCreateDTO userCreateDTO;
	private UserUpdateDTO userUpdateDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userCreateDTO = new UserCreateDTO();
		userCreateDTO.setUsername("testUser");
		userCreateDTO.setEmail("test@example.com");
		userCreateDTO.setPassword("password");
		userCreateDTO.setName("testUser");
		userCreateDTO.setFile(mockFile);

		userUpdateDTO = new UserUpdateDTO();
		userUpdateDTO.setName("Updated User");
		userUpdateDTO.setBio("Updated Bio");
		userUpdateDTO.setFile(mockFile);
	}

	@Test
	void testCreateModelFromDto() throws IOException {
		UserModel userModel = UserFactory.createModelFromDto(userCreateDTO);

		assertNotNull(userModel);
		assertEquals("testUser", userModel.getUsername());
		assertEquals("test@example.com", userModel.getEmail());
		assertNotNull(userModel.getPassword());
		assertTrue(BCryptUtil.checkPassword("password", userModel.getPassword()));
		assertEquals(LocalDate.now(), userModel.getCreationDate());
		assertEquals("testUser", userModel.getName());
	}

	@Test
	void testUpdateModelFromDto() {
		UserModel userModel = new UserModel();
		UserModel updatedUserModel = UserFactory.updateModelFromDto(userUpdateDTO, userModel);

		assertNotNull(updatedUserModel);
		assertEquals("Updated User", updatedUserModel.getName());
		assertEquals("Updated Bio", updatedUserModel.getBio());
	}

	@Test
	void testBuildList() {
		UserModel userModel = new UserModel();
		userModel.setId(1L);
		userModel.setName("Test User");
		userModel.setCreationDate(LocalDate.now());
		userModel.setUserCode("USER_CODE");

		List<UserDTO> userDTOs = UserFactory.buildList(List.of(userModel), 1L);

		assertNotNull(userDTOs);
		assertEquals(1, userDTOs.size());
		assertEquals("Test User", userDTOs.get(0).getLabel());
	}

	@Test
	void testHandleProfilePic() throws IOException {
		UserModel userModel = new UserModel();
		when(mockFile.getBytes()).thenReturn("imageBytes".getBytes());
		when(mockFile.getOriginalFilename()).thenReturn("profile.jpg");

		UserFactory.handleProfilePic(mockFile, userModel);

		assertNotNull(userModel.getUserProfile());
		assertArrayEquals("imageBytes".getBytes(), userModel.getUserProfile().getContent());
	}

	@Test
	void testHandleProfilePicWhenFileIsEmpty() throws IOException {
		UserModel userModel = new UserModel();
		when(mockFile.isEmpty()).thenReturn(true);

		UserFactory.handleProfilePic(mockFile, userModel);

		assertNull(userModel.getUserProfile());
	}

	@Test
	void testConvertByteArrayToBase64() {
		String base64 = UserFactory.convertByteArrayToBase64("imageBytes".getBytes());
		assertNotNull(base64);
		assertFalse(base64.isEmpty());
	}

	@Test
	void testEquals_SameObject() {
		UserInfoDTO userInfo = new UserInfoDTO(1L, new byte[] { 1, 2, 3 }, "John Doe", "Bio", "JD123");
		assertEquals(userInfo, userInfo);
	}

	@Test
	void testEquals_EqualObjects() {
		byte[] profilePic = { 1, 2, 3 };
		UserInfoDTO userInfo1 = new UserInfoDTO(1L, profilePic, "John Doe", "Bio", "JD123");
		UserInfoDTO userInfo2 = new UserInfoDTO(1L, profilePic, "John Doe", "Bio", "JD123");

		assertEquals(userInfo1, userInfo2);
	}

	@Test
	void testEquals_DifferentObjects() {
		byte[] profilePic1 = { 1, 2, 3 };
		byte[] profilePic2 = { 4, 5, 6 };
		UserInfoDTO userInfo1 = new UserInfoDTO(1L, profilePic1, "John Doe", "Bio", "JD123");
		UserInfoDTO userInfo2 = new UserInfoDTO(2L, profilePic2, "Jane Doe", "Other Bio", "JD456");

		assertNotEquals(userInfo1, userInfo2);
	}

	@Test
	void testEquals_NullObject() {
		UserInfoDTO userInfo = new UserInfoDTO(1L, new byte[] { 1, 2, 3 }, "John Doe", "Bio", "JD123");

		assertNotEquals(null, userInfo);
	}

	@Test
	void testEquals_DifferentClass() {
		UserInfoDTO userInfo = new UserInfoDTO(1L, new byte[] { 1, 2, 3 }, "John Doe", "Bio", "JD123");

		assertNotEquals("Some String", userInfo);
	}

	@Test
	void testHashCode_EqualObjects() {
		byte[] profilePic = { 1, 2, 3 };
		UserInfoDTO userInfo1 = new UserInfoDTO(1L, profilePic, "John Doe", "Bio", "JD123");
		UserInfoDTO userInfo2 = new UserInfoDTO(1L, profilePic, "John Doe", "Bio", "JD123");

		assertEquals(userInfo1.hashCode(), userInfo2.hashCode());
	}

	@Test
	void testHashCode_DifferentObjects() {
		byte[] profilePic1 = { 1, 2, 3 };
		byte[] profilePic2 = { 4, 5, 6 };
		UserInfoDTO userInfo1 = new UserInfoDTO(1L, profilePic1, "John Doe", "Bio", "JD123");
		UserInfoDTO userInfo2 = new UserInfoDTO(2L, profilePic2, "Jane Doe", "Other Bio", "JD456");

		assertNotEquals(userInfo1.hashCode(), userInfo2.hashCode());
	}

	@Test
	void testToString() {
		byte[] profilePic = { 1, 2, 3 };
		UserInfoDTO userInfo = new UserInfoDTO(1L, profilePic, "John Doe", "Bio", "JD123");

		String expectedString = "UserInfoDTO{id=1, profilePic=[1, 2, 3], name='John Doe', bio='Bio', userCode='JD123'}";
		assertEquals(expectedString, userInfo.toString());
	}
}
