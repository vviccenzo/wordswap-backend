package com.backend.wordswap.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserResponseDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserCreateDTO userCreateDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserModel userModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testUser");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("password");
        userCreateDTO.setName("testUser");

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1L);
        userUpdateDTO.setName("Updated User");
        userUpdateDTO.setBio("Updated Bio");

        userModel = new UserModel();
        userModel.setId(1L);
        userModel.setUsername("testUser");
        userModel.setEmail("test@example.com");
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(userModel));

        List<UserResponseDTO> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
    }

    @Test
    void testSave() throws IOException {
        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userCreateDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        UserDTO savedUser = this.userService.save(userCreateDTO);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getLabel());
        verify(userRepository).save(userModel);
    }

    @Test
    void testSaveWithExistingEmail() {
        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(Optional.of(userModel));

        assertThrows(UserEmailAlreadyExistsException.class, () -> userService.save(userCreateDTO));
    }

    @Test
    void testSaveWithExistingUsername() {
        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userCreateDTO.getUsername())).thenReturn(Optional.of(userModel));

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.save(userCreateDTO));
    }

    @Test
    void testUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        UserDTO updatedUser = this.userService.update(userUpdateDTO);

        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getLabel());
        verify(userRepository).save(userModel);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(userUpdateDTO));
    }

    @Test
    void testDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void testFindFriendsByUserId() {
        userModel.setFriends(List.of(userModel));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));

        List<UserDTO> friends = userService.findFriendsByUserId(1L);

        assertNotNull(friends);
        assertEquals(1, friends.size());
    }

    @Test
    void testFindFriendsByUserIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findFriendsByUserId(1L));
    }
}
