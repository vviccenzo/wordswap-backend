package com.backend.wordswap.auth.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

	private final LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping(path = "/login")
	public String login(@RequestParam("user") String user, @RequestParam("password") String password) {
		return this.loginService.login(user, password);
	}

}
