package com.backend.wordswap.user;

import com.backend.wordswap.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping(path = "/find-all", produces = MediaType.APPLICATION_JSON_VALUE)
	private List<UserResponseDTO> findAll() {
		return this.userService.findAll();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	private UserDTO save(@RequestBody UserCreateDTO dto) {
		return this.userService.save(dto);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	private UserDTO update(@RequestBody UserUpdateDTO dto) {
		return this.userService.update(dto);
	}

	@DeleteMapping
	private void deleteUser(@RequestParam("id") Long id) {
		this.userService.delete(id);
	}
}
