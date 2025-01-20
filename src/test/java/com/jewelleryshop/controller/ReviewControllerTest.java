package com.jewelleryshop.controller;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Review;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.ReviewRequest;
import com.jewelleryshop.service.ReviewService;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewControllerTest {

	@InjectMocks
	private ReviewController reviewController;

	@Mock
	private ReviewService reviewService;

	@Mock
	private UserService userService;

	private Review review;
	private User user;
	private ReviewRequest reviewRequest;
	private List<Review> reviewList;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Set up a mock user
		user = new User();
		user.setId(1L);
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john.doe@example.com");

		// Set up a mock review
		review = new Review();
		review.setId(1L);
		review.setReview("Great product!");
		review.setProduct(new Product());
		review.setUser(user);
		review.setCreatedAt(LocalDateTime.now());

		// Set up a mock ReviewRequest
		reviewRequest = new ReviewRequest();
		reviewRequest.setProductId(1L);
		reviewRequest.setReview("Great product!");

		// Set up a list of reviews
		reviewList = Arrays.asList(review);
	}

	@Test
	void testCreateReviewHandler() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenReturn(user);
		when(reviewService.createReview(eq(reviewRequest), eq(user))).thenReturn(review);

		// Act
		ResponseEntity<Review> response = reviewController.createReviewHandler(reviewRequest, jwt);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(review, response.getBody());
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(reviewService, times(1)).createReview(eq(reviewRequest), eq(user));
	}

	@Test
	void testCreateReviewHandler_UserException() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenThrow(UserException.class);

		// Act & Assert
		assertThrows(UserException.class, () -> reviewController.createReviewHandler(reviewRequest, jwt));
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(reviewService, times(0)).createReview(any(), any());
	}

	@Test
	void testCreateReviewHandler_ProductException() throws UserException, ProductException {
		// Arrange
		String jwt = "Bearer token";
		when(userService.findUserProfileByJwt(eq(jwt))).thenReturn(user);
		when(reviewService.createReview(eq(reviewRequest), eq(user))).thenThrow(ProductException.class);

		// Act & Assert
		assertThrows(ProductException.class, () -> reviewController.createReviewHandler(reviewRequest, jwt));
		verify(userService, times(1)).findUserProfileByJwt(eq(jwt));
		verify(reviewService, times(1)).createReview(eq(reviewRequest), eq(user));
	}

	@Test
	void testGetProductsReviewHandler() {
		// Arrange
		Long productId = 1L;
		when(reviewService.getAllReview(eq(productId))).thenReturn(reviewList);

		// Act
		ResponseEntity<List<Review>> response = reviewController.getProductsReviewHandler(productId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(reviewList, response.getBody());
		verify(reviewService, times(1)).getAllReview(eq(productId));
	}
}
