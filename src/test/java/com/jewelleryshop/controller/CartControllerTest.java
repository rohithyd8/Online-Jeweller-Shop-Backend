package com.jewelleryshop.controller;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.AddItemRequest;
import com.jewelleryshop.service.CartService;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class CartControllerTest {

	@Mock
	private CartService cartService;

	@Mock
	private UserService userService;

	@InjectMocks
	private CartController cartController;

	private User user;
	private Cart cart;
	private CartItem cartItem;
	private AddItemRequest addItemRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initializes mock objects

		// Initialize mock user
		user = new User();
		user.setId(1L);
		user.setEmail("testuser@example.com");

		// Initialize mock cart
		cart = new Cart();
		cart.setUser(user);

		// Initialize mock cart item
		cartItem = new CartItem();
		cartItem.setId(1L);
		cartItem.setQuantity(2);

		// Initialize AddItemRequest
		addItemRequest = new AddItemRequest();
		addItemRequest.setProductId(1L);
		addItemRequest.setQuantity(2);
	}

	@Test
	void testFindUserCartHandler() throws UserException {
		// Arrange
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user); // Mock user retrieval
		when(cartService.findUserCart(user.getId())).thenReturn(cart); // Mock cart retrieval

		// Act
		ResponseEntity<Cart> response = cartController.findUserCartHandler("Bearer validToken");

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(user.getEmail(), response.getBody().getUser().getEmail());
	}

	@Test
	void testAddItemToCart() throws UserException, ProductException {
		// Arrange
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user); // Mock user retrieval
		when(cartService.addCartItem(user.getId(), addItemRequest)).thenReturn(cartItem); // Mock item addition to cart

		// Act
		ResponseEntity<CartItem> response = cartController.addItemToCart(addItemRequest, "Bearer validToken");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(cartItem.getQuantity(), response.getBody().getQuantity());
	}
}
