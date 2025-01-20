package com.jewelleryshop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    // Create a logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);

    private OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrdersHandler() {
        logger.info("Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        logger.info("Fetched {} orders", orders.size());
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/confirmed")
    public ResponseEntity<Order> confirmedOrderHandler(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws OrderException {
        logger.info("Confirming order with ID: {}", orderId);
        Order order = orderService.confirmedOrder(orderId);
        logger.info("Order with ID: {} confirmed successfully", orderId);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Order> shippedOrderHandler(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws OrderException {
        logger.info("Shipping order with ID: {}", orderId);
        Order order = orderService.shippedOrder(orderId);
        logger.info("Order with ID: {} shipped successfully", orderId);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<Order> deliveredOrderHandler(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws OrderException {
        logger.info("Delivering order with ID: {}", orderId);
        Order order = orderService.deliveredOrder(orderId);
        logger.info("Order with ID: {} delivered successfully", orderId);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> canceledOrderHandler(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws OrderException {
        logger.info("Cancelling order with ID: {}", orderId);
        Order order = orderService.cancledOrder(orderId);
        logger.info("Order with ID: {} canceled successfully", orderId);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{orderId}/delete")
    public ResponseEntity<ApiResponse> deleteOrderHandler(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws OrderException {
        logger.info("Deleting order with ID: {}", orderId);
        orderService.deleteOrder(orderId);
        ApiResponse res = new ApiResponse("Order Deleted Successfully", true);
        logger.info("Order with ID: {} deleted successfully", orderId);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
}
