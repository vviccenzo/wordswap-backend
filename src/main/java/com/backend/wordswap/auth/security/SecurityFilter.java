package com.backend.wordswap.auth.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.wordswap.auth.TokenService;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenService tokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (("/auth/login".equals(path) || "/user".equals(path)) && "POST".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = this.recoverToken(request);

		if (token != null) {
			String login = this.tokenService.validateToken(token);
			Optional<UserModel> optUser = this.userRepository.findByUsername(login);
			if (optUser.isEmpty()) {
				throw new RuntimeException("User not valid.");
			}

			var authentication = new UsernamePasswordAuthenticationToken(optUser.get(), null, optUser.get().getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Bearer");
		if (authHeader == null) {
			return null;
		}

		return authHeader.replace("Bearer ", "");
	}

}
