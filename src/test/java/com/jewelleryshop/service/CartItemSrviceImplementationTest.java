package com.jewelleryshop.service;

import com.jewelleryshop.exception.CartItemException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.CartItemRepository;
import com.jewelleryshop.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartItemSrviceImplementationTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartItemServiceImplementation cartItemService;

    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the user service to return a test user
        user = new User();
        user.setId(1L);
        user.setFirstName("testUser");

        // Mock the cart service to return a test cart
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        // Mock the product service to return a test product
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setPrice(100);
        product.setDiscountedPrice(80);

        // Create a CartItem to be used in tests
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cartItem.setPrice(80);
        cartItem.setDiscountedPrice(80);
        cartItem.setUserId(user.getId());
    }

    @Test
    public void testCreateCartItem() {
        // When saving a cart item, it should be returned as created
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // Call service method
        CartItem createdCartItem = cartItemService.createCartItem(cartItem);

        // Verify the result
        assertNotNull(createdCartItem);
        assertEquals(cartItem.getProduct(), createdCartItem.getProduct());
        assertEquals(cartItem.getQuantity(), createdCartItem.getQuantity());
        assertEquals(cartItem.getPrice(), createdCartItem.getPrice());

        // Verify repository interaction
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    public void testUpdateCartItem() throws CartItemException, UserException {
        // Mock behavior when updating CartItem
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setQuantity(2);
        updatedCartItem.setPrice(160);
        updatedCartItem.setDiscountedPrice(160);

        // When CartItem exists, return the cart item from the repository
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userService.findUserById(cartItem.getUserId())).thenReturn(user);

        // When CartItem is saved, return the updated CartItem
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(updatedCartItem);

        // Call service method
        CartItem result = cartItemService.updateCartItem(user.getId(), cartItem.getId(), updatedCartItem);

        // Assert the updated values
        assertNotNull(result);
        assertEquals(updatedCartItem.getQuantity(), result.getQuantity());
        assertEquals(updatedCartItem.getPrice(), result.getPrice());

        // Verify repository interaction
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    public void testUpdateCartItem_UserException() throws UserException {
        // Simulate a different user attempting to update the CartItem
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setQuantity(2);

        // Mock the behavior when CartItem exists, but user IDs don't match
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userService.findUserById(cartItem.getUserId())).thenReturn(user);

        // Expect a CartItemException to be thrown
        assertThrows(CartItemException.class, () -> {
            cartItemService.updateCartItem(2L, cartItem.getId(), updatedCartItem); // Different user ID
        });
    }

    @Test
    public void testIsCartItemExist() {
        // Mock behavior when CartItem exists for the given product, cart, and user
        when(cartItemRepository.isCartItemExist(cart, product, "M", user.getId())).thenReturn(cartItem);

        // Call the service method to check if the CartItem exists
        CartItem result = cartItemService.isCartItemExist(cart, product, "M", user.getId());

        // Assert that the CartItem is returned
        assertNotNull(result);
        assertEquals(cartItem.getId(), result.getId());

        // Verify repository interaction
        verify(cartItemRepository, times(1)).isCartItemExist(cart, product, "M", user.getId());
    }

    @Test
    public void testRemoveCartItem() throws CartItemException, UserException {
        // Mock behavior for removing a cart item
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userService.findUserById(cartItem.getUserId())).thenReturn(user);

        // Simulate the deletion of the CartItem
        doNothing().when(cartItemRepository).deleteById(cartItem.getId());

        // Call the service method to remove the CartItem
        cartItemService.removeCartItem(user.getId(), cartItem.getId());

        // Verify repository interaction (CartItem is deleted)
        verify(cartItemRepository, times(1)).deleteById(cartItem.getId());
    }

   /* @Test
    public void testRemoveCartItem_UserException() throws UserException {
        // Simulate a different user attempting to remove the CartItem
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userService.findUserById(cartItem.getUserId())).thenReturn(user);

        // Expect a UserException to be thrown
        assertThrows(UserException.class, () -> {
            cartItemService.removeCartItem(2L, cartItem.getId()); // Different user ID
        });
    }*/
}
