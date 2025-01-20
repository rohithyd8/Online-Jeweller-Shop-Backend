package com.jewelleryshop.controller;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminOrderControllerTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private AdminOrderController adminOrderController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllOrdersHandler() {
		// Arrange
		List<Order> orders = Collections.singletonList(new Order());
		when(orderService.getAllOrders()).thenReturn(orders);

		// Act
		ResponseEntity<List<Order>> response = adminOrderController.getAllOrdersHandler();

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(orders, response.getBody());
		verify(orderService, times(1)).getAllOrders();
	}

	@Test
	void testConfirmedOrderHandler() throws OrderException {
		// Arrange
		Long orderId = 1L;
		Order order = new Order(); // Mock order
		when(orderService.confirmedOrder(orderId)).thenReturn(order);

		// Act
		ResponseEntity<Order> response = adminOrderController.ConfirmedOrderHandler(orderId, "Bearer validJwt");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(order, response.getBody());
		verify(orderService, times(1)).confirmedOrder(orderId);
	}

	@Test
	void testShippedOrderHandler() throws OrderException {
		// Arrange
		Long orderId = 1L;
		Order order = new Order(); // Mock order
		when(orderService.shippedOrder(orderId)).thenReturn(order);

		// Act
		ResponseEntity<Order> response = adminOrderController.shippedOrderHandler(orderId, "Bearer validJwt");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(order, response.getBody());
		verify(orderService, times(1)).shippedOrder(orderId);
	}

	@Test
	void testDeliveredOrderHandler() throws OrderException {
		// Arrange
		Long orderId = 1L;
		Order order = new Order(); // Mock order
		when(orderService.deliveredOrder(orderId)).thenReturn(order);

		// Act
		ResponseEntity<Order> response = adminOrderController.deliveredOrderHandler(orderId, "Bearer validJwt");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(order, response.getBody());
		verify(orderService, times(1)).deliveredOrder(orderId);
	}

	@Test
	void testCanceledOrderHandler() throws OrderException {
		// Arrange
		Long orderId = 1L;
		Order order = new Order(); // Mock order
		when(orderService.cancledOrder(orderId)).thenReturn(order);

		// Act
		ResponseEntity<Order> response = adminOrderController.canceledOrderHandler(orderId, "Bearer validJwt");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(order, response.getBody());
		verify(orderService, times(1)).cancledOrder(orderId);
	}

	@Test
	void testDeleteOrderHandler() throws OrderException {
		// Arrange
		Long orderId = 1L;
		ApiResponse expectedResponse = new ApiResponse("Order Deleted Successfully", true);

		// Mock the delete method to do nothing
		doNothing().when(orderService).deleteOrder(orderId);

		// Act
		ResponseEntity<ApiResponse> response = adminOrderController.deleteOrderHandler(orderId, "Bearer validJwt");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
		assertTrue(response.getBody().isStatus());
		verify(orderService, times(1)).deleteOrder(orderId); // Verify that the delete method was called once
	}

	@Test
	void testDeleteOrderHandlerWithException() throws OrderException {
		// Arrange
		Long orderId = 1L;
		doThrow(new OrderException("Order not found")).when(orderService).deleteOrder(orderId);

		// Act & Assert
		OrderException thrownException = assertThrows(OrderException.class, () -> {
			adminOrderController.deleteOrderHandler(orderId, "Bearer validJwt");
		});
		assertEquals("Order not found", thrownException.getMessage());
	}
}
