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

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDTO save(
			@RequestParam(name = "username") String username, 
			@RequestParam(name = "password") String password,
			@RequestParam(name = "email") String email, 
			@RequestParam(name = "file", required = false) MultipartFile file, 
			@RequestParam(name = "name") String name) throws IOException 
	{
		return this.userService.save(new UserCreateDTO(name, email, username, password, file));
	}

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDTO update(
        @RequestParam Long id,
        @RequestParam String name,
        @RequestParam String bio,
        @RequestParam(required = false) MultipartFile file
    ) throws IOException {
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
