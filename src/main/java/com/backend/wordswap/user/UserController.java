package com.backend.wordswap.user;

import com.backend.wordswap.user.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(path = "/find-all", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserResponseDTO> findAll() {
		return this.userService.findAll();
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDTO save(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
		return this.userService.save(new UserCreateDTO(username, email, password, file));
	}

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDTO update(
        @RequestParam Long id,
        @RequestParam String name,
        @RequestParam String bio,
        @RequestParam(required = false) MultipartFile file
    ) {
		return this.userService.update(new UserUpdateDTO(id, name, bio, file));
	}

	@DeleteMapping
	public void deleteUser(@RequestParam("id") Long id) {
		this.userService.delete(id);
	}

	@GetMapping(path = "/find-friends", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserDTO> findFriends(@RequestParam("userId") Long userId) {
		return this.userService.findFriendsByUserId(userId);
	}
}
