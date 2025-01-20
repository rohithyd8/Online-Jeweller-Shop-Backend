package com.jewelleryshop.controller;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Address;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.OrderService;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class OrderControllerTest {

	@InjectMocks
	private OrderController orderController;

	@Mock
	private OrderService orderService;

	@Mock
	private UserService userService;

	private User user;
	private Address shippingAddress;
	private Order order;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initialize mocks
		user = new User();
		user.setId(1L);
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john.doe@example.com");

		shippingAddress = new Address();
		shippingAddress.setStreetAddress("123 Main St");
		shippingAddress.setCity("Springfield");
		shippingAddress.setState("IL");
		shippingAddress.setZipCode("62701");
		order = new Order();
		order.setId(1L);
		order.setUser(user);
		order.setShippingAddress(shippingAddress);
	}

	@Test
	void testCreateOrderHandler() throws UserException {
		// Arrange
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		when(orderService.createOrder(eq(user), eq(shippingAddress))).thenReturn(order);

		// Act
		ResponseEntity<Order> response = orderController.createOrderHandler(shippingAddress, "Bearer token");

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1L, response.getBody().getId());
		assertEquals(user, response.getBody().getUser());
		verify(orderService, times(1)).createOrder(eq(user), eq(shippingAddress));
	}

	@Test
	void testUsersOrderHistoryHandler() throws OrderException, UserException {
		// Arrange
		List<Order> orders = Arrays.asList(order);
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		when(orderService.usersOrderHistory(eq(user.getId()))).thenReturn(orders);

		// Act
		ResponseEntity<List<Order>> response = orderController.usersOrderHistoryHandler("Bearer token");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().size());
		assertEquals(order, response.getBody().get(0));
		verify(orderService, times(1)).usersOrderHistory(eq(user.getId()));
	}

	@Test
	void testFindOrderHandler() throws OrderException, UserException {
		// Arrange
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		when(orderService.findOrderById(eq(1L))).thenReturn(order);

		// Act
		ResponseEntity<Order> response = orderController.findOrderHandler(1L, "Bearer token");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1L, response.getBody().getId());
		assertEquals(user, response.getBody().getUser());
		verify(orderService, times(1)).findOrderById(eq(1L));
	}
}
