package com.jewelleryshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.response.ApiResponse;

@RestController
public class HomeController {

    // Create a logger instance for this controller class
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public ResponseEntity<ApiResponse> homeController() {

        // Log that the home endpoint was accessed
        logger.info("Accessing home endpoint");

        // Creating the response
        ApiResponse res = new ApiResponse("Welcome To E-Commerce System", true);

        // Log that the response is being sent
        logger.info("Responding with message: {}", res.getMessage());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
