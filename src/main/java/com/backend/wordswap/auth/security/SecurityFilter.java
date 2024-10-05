package com.backend.wordswap.auth.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.wordswap.auth.token.TokenService;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;

	private final TokenService tokenService;

	public SecurityFilter(UserRepository userRepository, TokenService tokenService) {
		this.userRepository = userRepository;
		this.tokenService = tokenService;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (this.isAllowedPath(request) && this.isAllowedMethod(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = this.recoverToken(request);

		if (token != null) {
			String login = this.tokenService.validateToken(token);
			Optional<UserModel> optUser = this.userRepository.findByUsername(login);
			if (optUser.isEmpty()) {
				throw new UserNotFoundException("User not valid.");
			}

			var authentication = new UsernamePasswordAuthenticationToken(optUser.get(), null,
					optUser.get().getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}

	public boolean isAllowedPath(ServletRequest request) {
	    String path = ((HttpServletRequest) request).getRequestURI();
	    if (path == null) {
	        return false;
	    }

	    return "/auth/login".equals(path) 
	            || "/swagger-ui/**".equals(path) 
	            || "/v3/api-docs/**".equals(path)
	            || "/swagger-ui.html".equals(path) 
	            || path.contains("/ws") 
	            || "/user".equals(path)
	            || path.contains("/translation");
	}

	public boolean isAllowedMethod(ServletRequest request) {
		String method = ((HttpServletRequest) request).getMethod();
		return "POST".equalsIgnoreCase(method) || "GET".equalsIgnoreCase(method);
	}
}
