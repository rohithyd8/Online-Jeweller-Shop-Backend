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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private AdminUserController adminUserController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllUsersSuccess() throws UserException {
		// Arrange
		List<User> users = Collections.singletonList(new User());
		when(userService.findAllUsers()).thenReturn(users);

		// Act
		ResponseEntity<List<User>> response = adminUserController.getAllUsers("Bearer validJwt");

		// Assert:
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(users, response.getBody());

		// findAllUsers method was called once
		verify(userService, times(1)).findAllUsers();
	}
}
