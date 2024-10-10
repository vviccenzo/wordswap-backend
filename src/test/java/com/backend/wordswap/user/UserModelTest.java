package com.backend.wordswap.user;

import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

	private UserModel user;

	@BeforeEach
	void setUp() {
		user = new UserModel();
		user.setUsername("testUser");
		user.setPassword("password123");
		user.setEmail("testUser@example.com");
		user.setUserCode("U123456");
		user.setName("Test User");
		user.setBio("This is a test user.");
		user.setCreationDate(LocalDate.now());
	}

	@Test
	void testGetAuthoritiesForUserRole() {
		user.setRole(UserRole.USER);

		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	void testGetAuthoritiesForAdminRole() {
		user.setRole(UserRole.ADMIN);

		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

		assertNotNull(authorities);
		assertEquals(2, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	void testIsAccountNonExpired() {
		assertTrue(user.isAccountNonExpired());
	}

	@Test
	void testIsAccountNonLocked() {
		assertTrue(user.isAccountNonLocked());
	}

	@Test
	void testIsCredentialsNonExpired() {
		assertTrue(user.isCredentialsNonExpired());
	}

	@Test
	void testIsEnabled() {
		assertTrue(user.isEnabled());
	}

	@Test
	void testUserFields() {
		assertEquals("testUser", user.getUsername());
		assertEquals("password123", user.getPassword());
		assertEquals("testUser@example.com", user.getEmail());
		assertEquals("U123456", user.getUserCode());
		assertEquals("Test User", user.getName());
		assertEquals("This is a test user.", user.getBio());
		assertNotNull(user.getCreationDate());
	}
}
