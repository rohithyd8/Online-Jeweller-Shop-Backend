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
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.Review;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.RatingRequest;
import com.jewelleryshop.service.RatingServices;
import com.jewelleryshop.service.UserService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    
    private UserService userService;
    private RatingServices ratingServices;
    
    public RatingController(UserService userService, RatingServices ratingServices) {
        this.ratingServices = ratingServices;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Rating> createRatingHandler(@RequestBody RatingRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        logger.info("Received rating creation request for user with JWT: {}", jwt);

        try {
            User user = userService.findUserProfileByJwt(jwt);
            logger.info("User profile retrieved: {}", user.getEmail());

            Rating rating = ratingServices.createRating(req, user);
            logger.info("Rating created successfully for product with ID: {}", req.getProductId());

            return new ResponseEntity<>(rating, HttpStatus.ACCEPTED);
        } catch (UserException | ProductException e) {
            logger.error("Error creating rating: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductsReviewHandler(@PathVariable Long productId) {
        logger.info("Fetching reviews for product with ID: {}", productId);

        List<Rating> ratings = ratingServices.getProductsRating(productId);
        logger.info("Fetched {} ratings for product with ID: {}", ratings.size(), productId);

        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
}
