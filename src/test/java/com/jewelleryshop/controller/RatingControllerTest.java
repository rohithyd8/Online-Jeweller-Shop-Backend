package com.jewelleryshop.controller;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.RatingRequest;
import com.jewelleryshop.service.RatingServices;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RatingControllerTest {

	@InjectMocks
	private RatingController ratingController;

	@Mock
	private UserService userService;

	@Mock
	private RatingServices ratingServices;

	private Rating rating;
	private User user;
	private RatingRequest ratingRequest;
	private List<Rating> ratingList;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Set up a mock user
		user = new User();
		user.setId(1L);
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john.doe@example.com");

		// Set up a mock Rating
		rating = new Rating();
		rating.setId(1L);
		rating.setRating(5);
		rating.setProduct(new Product());
		rating.setUser(user);

		// Set up a mock RatingRequest
		ratingRequest = new RatingRequest();
		ratingRequest.setProductId(1L);
		ratingRequest.setRating(5);

		// Set up a list of ratings
		ratingList = Arrays.asList(rating);
	}

	@Test
	void testCreateRatingHandler() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenReturn(user);
		when(ratingServices.createRating(eq(ratingRequest), eq(user))).thenReturn(rating);

		// Act
		ResponseEntity<Rating> response = ratingController.createRatingHandler(ratingRequest, jwt);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(rating, response.getBody());
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(ratingServices, times(1)).createRating(eq(ratingRequest), eq(user));
	}

	@Test
	void testCreateRatingHandler_UserException() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenThrow(UserException.class);

		// Act & Assert
		assertThrows(UserException.class, () -> ratingController.createRatingHandler(ratingRequest, jwt));
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(ratingServices, times(0)).createRating(any(), any());
	}

	@Test
	void testCreateRatingHandler_ProductException() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenReturn(user);
		when(ratingServices.createRating(eq(ratingRequest), eq(user))).thenThrow(ProductException.class);

		// Act & Assert
		assertThrows(ProductException.class, () -> ratingController.createRatingHandler(ratingRequest, jwt));
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(ratingServices, times(1)).createRating(eq(ratingRequest), eq(user));
	}

	@Test
	void testGetProductsReviewHandler() {
		// Arrange
		Long productId = 1L;
		when(ratingServices.getProductsRating(eq(productId))).thenReturn(ratingList);

		// Act
		ResponseEntity<List<Rating>> response = ratingController.getProductsReviewHandler(productId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(ratingList, response.getBody());
		verify(ratingServices, times(1)).getProductsRating(eq(productId));
	}
}
