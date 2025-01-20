package com.jewelleryshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHandler(@RequestHeader("Authorization") String jwt) throws UserException {

		// Log the incoming request
		logger.info("Received request to get user profile with JWT: {}", jwt);

		try {
			// Fetch the user profile using JWT
			User user = userService.findUserProfileByJwt(jwt);
			
			// Log the successful retrieval of the user profile
			logger.info("User profile retrieved successfully for user: {}", user.getEmail());

			return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
		} catch (UserException e) {
			// Log the error if an exception occurs
			logger.error("Error retrieving user profile: {}", e.getMessage());
			throw e;  // Re-throw the exception to be handled by the global exception handler
		}
	}
}
