package com.backend.wordswap.auth.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.auth.dto.AuthDTO;

@RestController
@RequestMapping("/auth")
public class LoginController {

	private final LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping(path = "/login")
	public ResponseEntity<?> login(@RequestParam("user") String user, @RequestParam("password") String password) {
	    try {
	        AuthDTO authDTO = this.loginService.login(user, password);
	        return ResponseEntity.ok(authDTO);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}


}
