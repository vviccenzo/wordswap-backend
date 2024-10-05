package com.backend.wordswap.auth.login;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.auth.dto.AuthDTO;
import com.backend.wordswap.friendship.exception.FriendshipAlreadySendedException;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;

@RestController
@RequestMapping("/auth")
public class LoginController {

	private final LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping(path = "/login")
	public AuthDTO login(@RequestParam("user") String user, @RequestParam("password") String password) {
		return this.loginService.login(user, password);
	}

	@GetMapping(path = "/test-controller")
	public ResponseEntity<String> testEndpoint() {
		return ResponseEntity.ok("CORS Test Successful");
	}

    @GetMapping("/friendship-already-sended")
    public void triggerFriendshipAlreadySendedException() {
        throw new FriendshipAlreadySendedException("Friendship request already sent");
    }

    @GetMapping("/user-not-found")
    public void triggerUserNotFoundException() {
        throw new UserNotFoundException("User not found");
    }

    @GetMapping("/username-already-exists")
    public void triggerUsernameAlreadyExistsException() {
        throw new UsernameAlreadyExistsException("Username already exists");
    }

    @GetMapping("/user-email-already-exists")
    public void triggerUserEmailAlreadyExistsException() {
        throw new UserEmailAlreadyExistsException("User email already exists");
    }
}
