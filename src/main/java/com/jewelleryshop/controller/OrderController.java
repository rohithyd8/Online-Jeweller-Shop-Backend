package com.jewelleryshop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Address;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.OrderService;
import com.jewelleryshop.service.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private OrderService orderService;
    private UserService userService;
    
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }
    
    @PostMapping("/")
    public ResponseEntity<Order> createOrderHandler(@RequestBody Address shippingAddress,
            @RequestHeader("Authorization") String jwt) throws UserException {
        
        logger.info("Received request to create order for user with JWT: {}", jwt);

        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.createOrder(user, shippingAddress);

        logger.info("Created order with ID: {}", order.getId());

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistoryHandler(@RequestHeader("Authorization") String jwt) 
            throws OrderException, UserException {
        
        logger.info("Fetching order history for user with JWT: {}", jwt);

        User user = userService.findUserProfileByJwt(jwt);
        List<Order> orders = orderService.usersOrderHistory(user.getId());

        logger.info("Found {} orders for user with ID: {}", orders.size(), user.getId());

        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> findOrderHandler(@PathVariable Long orderId, @RequestHeader("Authorization") String jwt)
            throws OrderException, UserException {
        
        logger.info("Fetching details for order with ID: {} for user with JWT: {}", orderId, jwt);

        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.findOrderById(orderId);

        if (order != null) {
            logger.info("Found order with ID: {}", order.getId());
        } else {
            logger.warn("Order with ID: {} not found", orderId);
        }

        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }
}
