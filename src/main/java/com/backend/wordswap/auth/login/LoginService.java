package com.backend.wordswap.auth.login;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.wordswap.auth.TokenService;
import com.backend.wordswap.auth.dto.AuthDTO;
import com.backend.wordswap.auth.util.BCryptUtil;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.dto.UserInfoDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.InvalidCredentialsException;
import com.backend.wordswap.user.exception.UserNotFoundException;

@Service
public class LoginService {

	private final UserRepository userRepository;

	private final TokenService tokenService;

	public LoginService(UserRepository userRepository, TokenService tokenService) {
		this.userRepository = userRepository;
		this.tokenService = tokenService;
	}

	public AuthDTO login(String user, String password) {
		Optional<UserModel> optUser = this.userRepository.findByUsername(user);
		if (optUser.isEmpty()) {
			throw new UserNotFoundException("User not found.");
		}

		UserModel userModel = optUser.get();
		boolean isPasswordValid = BCryptUtil.checkPassword(password, userModel.getPassword());
		if (!isPasswordValid) {
			throw new InvalidCredentialsException("Invalid credentials.");
		}

		String token = this.tokenService.generateToken(userModel);
		byte[] profilePic = Objects.nonNull(userModel.getUserProfile()) ? userModel.getUserProfile().getContent()
				: null;
		UserInfoDTO userInfo = new UserInfoDTO(userModel.getId(), profilePic);

		return new AuthDTO(token, userInfo);
	}

}