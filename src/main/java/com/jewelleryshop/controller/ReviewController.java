package com.jewelleryshop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Review;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.ReviewRequest;
import com.jewelleryshop.service.ReviewService;
import com.jewelleryshop.service.UserService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
	
	private ReviewService reviewService;
	private UserService userService;
	
	public ReviewController(ReviewService reviewService, UserService userService) {
		this.reviewService = reviewService;
		this.userService = userService;
	}

	@PostMapping("/create")
	public ResponseEntity<Review> createReviewHandler(@RequestBody ReviewRequest req, @RequestHeader("Authorization") String jwt) 
			throws UserException, ProductException {
		
		logger.info("Received review creation request for product ID: {} from user with JWT: {}", req.getProductId(), jwt);

		try {
			User user = userService.findUserProfileByJwt(jwt);
			logger.info("User profile retrieved: {}", user.getEmail());

			// Log the review details
			logger.info("Review being created for product ID: {} with review: {}", req.getProductId(), req.getReview());

			Review review = reviewService.createReview(req, user);
			logger.info("Review created successfully for product ID: {}", req.getProductId());

			return new ResponseEntity<>(review, HttpStatus.ACCEPTED);
		} catch (UserException | ProductException e) {
			logger.error("Error creating review: {}", e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Review>> getProductsReviewHandler(@PathVariable Long productId) {
		logger.info("Fetching reviews for product with ID: {}", productId);

		List<Review> reviews = reviewService.getAllReview(productId);
		logger.info("Fetched {} reviews for product with ID: {}", reviews.size(), productId);

		return new ResponseEntity<>(reviews, HttpStatus.OK);
	}
}
