package com.backend.wordswap.auth.token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.auth.token.definition.TokenResponseDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth/token")
public class TokenController {

	private final TokenService tokenService;
	
	@GetMapping(path = "/validate-token")
	public TokenResponseDTO isTokenValid(@RequestParam("token") String token) {
		return new TokenResponseDTO(!this.tokenService.validateToken(token).isEmpty());
	}
	
}
