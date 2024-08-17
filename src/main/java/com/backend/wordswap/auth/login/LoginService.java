package com.backend.wordswap.auth.login;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.wordswap.auth.TokenService;
import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;

@Service
public class LoginService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenService tokenService;

	public String login(String user, String password) {
		Optional<UserModel> optUser = this.userRepository.findByUsername(user);
		if (optUser.isEmpty()) {
			throw new RuntimeException("User not founded.");
		}

		UserModel userModel = optUser.get();
		boolean isPasswordValid = BCryptUtil.checkPassword(password, userModel.getPassword());
		if (!isPasswordValid) {
			throw new RuntimeException("Invalid credentials.");
		}

		return this.tokenService.generateToken(userModel);
	}

}
