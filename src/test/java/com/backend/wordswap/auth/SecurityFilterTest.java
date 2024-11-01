package com.backend.wordswap.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import com.backend.wordswap.domain.security.SecurityFilter;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.entity.UserRole;
import com.backend.wordswap.user.exception.UserNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setUsername("testUser");
        user.setRole(UserRole.USER);
    }

    @Test
    void testDoFilterInternalWithValidToken() throws Exception {
        String token = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(user.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws Exception {
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn("invalidUser");
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            securityFilter.doFilterInternal(request, response, filterChain);
        });

        assertEquals("User not valid.", exception.getMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testIsAllowedPath() {
        when(request.getRequestURI()).thenReturn("/auth/login");
        assertTrue(securityFilter.isAllowedPath(request));

        when(request.getRequestURI()).thenReturn("/not/allowed");
        assertFalse(securityFilter.isAllowedPath(request));
    }

    @Test
    void testIsAllowedMethod() {
        when(request.getMethod()).thenReturn("POST");
        assertTrue(securityFilter.isAllowedMethod(request));

        when(request.getMethod()).thenReturn("PUT");
        assertFalse(securityFilter.isAllowedMethod(request));
    }
}
