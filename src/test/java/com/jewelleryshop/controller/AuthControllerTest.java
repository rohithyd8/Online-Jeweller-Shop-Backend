package com.jewelleryshop.controller;

import com.jewelleryshop.config.JwtTokenProvider;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.UserRepository;
import com.jewelleryshop.request.LoginRequest;
import com.jewelleryshop.response.AuthResponse;
import com.jewelleryshop.service.CartService;
import com.jewelleryshop.service.CustomUserDetails;
import com.jewelleryshop.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private CustomUserDetails customUserDetails;

	@Mock
	private CartService cartService;

	@InjectMocks
	private AuthController authController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// Test for successful signup
	@Test
	void testSignup() throws UserException {
		// Arrange
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setRole(UserRole.ROLE_CUSTOMER.name());

		when(userRepository.findByEmail(user.getEmail())).thenReturn(null); // No existing user
		when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockedToken");

		// Act
		ResponseEntity<AuthResponse> response = authController.createUserHandler(user);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody().getJwt());
		assertTrue(response.getBody().getStatus());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(any(User.class));
	}

	// Test for signup with email already used
	@Test
	void testSignupWithEmailExists() throws UserException {
		// Arrange
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userRepository.findByEmail(user.getEmail())).thenReturn(user); // Email already exists

		// Act & Assert
		UserException thrown = assertThrows(UserException.class, () -> {
			authController.createUserHandler(user);
		});
		assertEquals("Email Is Already Used With Another Account", thrown.getMessage());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
	}

	// Test for successful signin
	@Test
	void testSignin() {
		// Arrange
		String email = "test@example.com";
		String password = "password";
		LoginRequest loginRequest = new LoginRequest(email, password);

		UserDetails userDetails = mock(UserDetails.class);
		when(customUserDetails.loadUserByUsername(email)).thenReturn(userDetails);
		when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(true);
		when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockedToken");

		// Act
		ResponseEntity<AuthResponse> response = authController.signin(loginRequest);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody().getJwt());
		assertTrue(response.getBody().getStatus());
		verify(customUserDetails, times(1)).loadUserByUsername(email);
	}

	// Test for signin with invalid credentials
	@Test
	void testSigninWithInvalidCredentials() {
		// Arrange
		String email = "test@example.com";
		String password = "password";
		LoginRequest loginRequest = new LoginRequest(email, password);

		when(customUserDetails.loadUserByUsername(email)).thenReturn(null); // User not found

		// Act & Assert
		BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> {
			authController.signin(loginRequest);
		});
		assertEquals("Invalid username or password", thrown.getMessage());
		verify(customUserDetails, times(1)).loadUserByUsername(email);
	}
}
