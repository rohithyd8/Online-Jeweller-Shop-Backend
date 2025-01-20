package com.jewelleryshop.controller;

import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initialize mocks

		// Creating a mock user
		user = new User();
		user.setId(1L);
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john.doe@example.com");
	}

	@Test
	void testGetUserProfileHandler() throws UserException {
		// Arrange
		String jwt = "Bearer valid_jwt_token";

		// Mocking the behavior of the userService
		when(userService.findUserProfileByJwt(jwt)).thenReturn(user);

		// Act
		ResponseEntity<User> response = userController.getUserProfileHandler(jwt);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()); // Check if status is 202
		assertNotNull(response.getBody()); // Check if response body is not null
		assertEquals(user.getId(), response.getBody().getId()); // Check if returned user matches the mock user
		assertEquals(user.getFirstName(), response.getBody().getFirstName()); // Check first name
		assertEquals(user.getLastName(), response.getBody().getLastName()); // Check last name
		assertEquals(user.getEmail(), response.getBody().getEmail()); // Check email

		verify(userService, times(1)).findUserProfileByJwt(jwt);
	}

	@Test
	void testGetUserProfileHandler_UserException() throws UserException {
		// Arrange
		String jwt = "Bearer invalid_jwt_token";

		// Mocking the behavior to throw UserException
		when(userService.findUserProfileByJwt(jwt)).thenThrow(UserException.class);

		// Act & Assert
		assertThrows(UserException.class, () -> userController.getUserProfileHandler(jwt));
	}
}