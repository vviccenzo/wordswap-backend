package com.backend.wordswap.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BCryptUtil {

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static String encryptPassword(String plainPassword) {
		return passwordEncoder.encode(plainPassword);
	}

	public static boolean checkPassword(String plainPassword, String encryptedPassword) {
		return passwordEncoder.matches(plainPassword, encryptedPassword);
	}

}
