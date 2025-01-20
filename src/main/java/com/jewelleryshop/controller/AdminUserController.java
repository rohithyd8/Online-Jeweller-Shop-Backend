package com.jewelleryshop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/admin")
public class AdminUserController {

    // Create a logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) throws UserException {

        logger.info("Received request to fetch all users. Authorization token: {}", jwt);

        List<User> users = userService.findAllUsers();
		logger.info("Fetched {} users", users.size());
		return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
    }
}
