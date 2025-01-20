package com.jewelleryshop.controller;

import com.jewelleryshop.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

	@InjectMocks
	private HomeController homeController; // Injecting the controller

	// Initialize mocks before each test
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testHomeController() {
		// Act
		ResponseEntity<ApiResponse> response = homeController.homeController();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Welcome To E-Commerce System", response.getBody().getMessage());
		assertEquals(true, response.getBody().isStatus());
	}
}
