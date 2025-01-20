package com.jewelleryshop.controller;

import com.jewelleryshop.exception.CartItemException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.CartItemService;
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

class CartItemControllerTest {

	@Mock
	private CartItemService cartItemService;

	@Mock
	private UserService userService;

	@InjectMocks
	private CartItemController cartItemController;

	private User user;
	private CartItem cartItem;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// Create mock user and cart item for the tests
		user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setFirstName("John");
		user.setLastName("Doe");

		cartItem = new CartItem();
		cartItem.setId(1L);
		cartItem.setQuantity(2);
	}

	@Test
	void testDeleteCartItemHandler() throws CartItemException, UserException {
		// Arrange
		Long cartItemId = 1L;
		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		doNothing().when(cartItemService).removeCartItem(user.getId(), cartItemId);

		// Act
		ResponseEntity<ApiResponse> response = cartItemController.deleteCartItemHandler(cartItemId, "Bearer token");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals("Item Remove From Cart", response.getBody().getMessage());
		verify(userService, times(1)).findUserProfileByJwt(anyString());
		verify(cartItemService, times(1)).removeCartItem(user.getId(), cartItemId);
	}

	@Test
	void testUpdateCartItemHandler() throws CartItemException, UserException {
		// Arrange
		Long cartItemId = 1L;
		CartItem updatedCartItem = new CartItem();
		updatedCartItem.setId(1L);
		updatedCartItem.setQuantity(3);

		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		when(cartItemService.updateCartItem(user.getId(), cartItemId, updatedCartItem)).thenReturn(updatedCartItem);

		// Act
		ResponseEntity<CartItem> response = cartItemController.updateCartItemHandler(cartItemId, updatedCartItem,
				"Bearer token");

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(updatedCartItem.getQuantity(), response.getBody().getQuantity());
		verify(userService, times(1)).findUserProfileByJwt(anyString());
		verify(cartItemService, times(1)).updateCartItem(user.getId(), cartItemId, updatedCartItem);
	}

	@Test
	void testDeleteCartItemHandler_UserException() throws CartItemException, UserException {
		// Arrange
		Long cartItemId = 1L;
		when(userService.findUserProfileByJwt(anyString())).thenThrow(new UserException("User not found"));

		// Act & Assert
		UserException thrown = assertThrows(UserException.class, () -> {
			cartItemController.deleteCartItemHandler(cartItemId, "Bearer invalid-token");
		});
		assertEquals("User not found", thrown.getMessage());
		verify(userService, times(1)).findUserProfileByJwt(anyString());
	}

	@Test
	void testUpdateCartItemHandler_CartItemException() throws CartItemException, UserException {
		// Arrange
		Long cartItemId = 1L;
		CartItem updatedCartItem = new CartItem();
		updatedCartItem.setId(1L);
		updatedCartItem.setQuantity(3);

		when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
		when(cartItemService.updateCartItem(user.getId(), cartItemId, updatedCartItem))
				.thenThrow(new CartItemException("CartItem update failed"));

		// Act & Assert
		CartItemException thrown = assertThrows(CartItemException.class, () -> {
			cartItemController.updateCartItemHandler(cartItemId, updatedCartItem, "Bearer token");
		});
		assertEquals("CartItem update failed", thrown.getMessage());
		verify(userService, times(1)).findUserProfileByJwt(anyString());
		verify(cartItemService, times(1)).updateCartItem(user.getId(), cartItemId, updatedCartItem);
	}
}
