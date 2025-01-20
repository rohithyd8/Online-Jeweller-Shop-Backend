package com.jewelleryshop.service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.CartRepository;
import com.jewelleryshop.request.AddItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CartServiceImplementationTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImplementation cartService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a test user
        user = new User();
        user.setId(1L);
        user.setFirstName("testUser");

        // Initialize a test cart with user
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());  // Start with an empty cart

        // Initialize a test product
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setPrice(100);
        product.setDiscountedPrice(80);

        // Initialize a test cart item for adding to cart
        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cartItem.setPrice(80);
        cartItem.setSize("M");
        cartItem.setUserId(user.getId());
    }

    @Test
    public void testCreateCart() {
        // Mock the CartRepository to return the created cart
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Call the service method to create the cart
        Cart createdCart = cartService.createCart(user);

        // Assert that the created cart is correct
        assertNotNull(createdCart);
        assertEquals(user, createdCart.getUser());
        assertTrue(createdCart.getCartItems().isEmpty());  // Cart should be empty initially

        // Verify the save method was called once
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

   /* @Test
    public void testFindUserCart() {
        // Add cart item to the cart to simulate existing cart
        cart.getCartItems().add(cartItem);
        
        // Mock the CartRepository to return the user's cart
        when(cartRepository.findByUserId(user.getId())).thenReturn(cart);

        // Call the service method to calculate the cart totals
        Cart updatedCart = cartService.findUserCart(user.getId());

        // Assert the cart total calculations
        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getTotalItem());  // Check that the total items in the cart are correct
        assertEquals(80, updatedCart.getTotalPrice());  // Check that the total price is correct
        assertEquals(80, updatedCart.getTotalDiscountedPrice());  // Check that the total discounted price is correct
        assertEquals(0, updatedCart.getDiscounte());  // No discount in this case

        // Verify the interaction with CartRepository
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(cartRepository, times(1)).save(updatedCart);  // Ensure the updated cart was saved
  

    @Test
    public void testAddCartItem() throws ProductException {
        // Prepare AddItemRequest with dynamic values
        AddItemRequest addItemRequest = new AddItemRequest();
        addItemRequest.setProductId(1L);
        addItemRequest.setQuantity(2);  // Adding 2 items
        addItemRequest.setSize("M");

        // Mock ProductService to return the test product
        when(productService.findProductById(addItemRequest.getProductId())).thenReturn(product);

        // Mock CartRepository to return the user's cart
        when(cartRepository.findByUserId(user.getId())).thenReturn(cart);

        // Mock CartItemService to check for the cart item existence
        when(cartItemService.isCartItemExist(cart, product, addItemRequest.getSize(), user.getId())).thenReturn(null);

        // Mock CartItemService to create a new cart item
        when(cartItemService.createCartItem(any(CartItem.class))).thenReturn(cartItem);

        // Call the service method to add the cart item
        CartItem addedCartItem = cartService.addCartItem(user.getId(), addItemRequest);

        // Assert the added cart item details
        assertNotNull(addedCartItem);
        assertEquals(2, addedCartItem.getQuantity());  // Quantity should be 2
        assertEquals("M", addedCartItem.getSize());  // Size should be "M"
        assertEquals(160, addedCartItem.getPrice());  // Price should be 2 * 80 (discounted price)

        // Verify the interactions with services
        verify(productService, times(1)).findProductById(addItemRequest.getProductId());
        verify(cartItemService, times(1)).isCartItemExist(cart, product, addItemRequest.getSize(), user.getId());
        verify(cartItemService, times(1)).createCartItem(any(CartItem.class));  // Ensure a new cart item is created
    }*/

    @Test
    public void testAddCartItem_AlreadyExists() throws ProductException {
        // Prepare AddItemRequest for adding a cart item
        AddItemRequest addItemRequest = new AddItemRequest();
        addItemRequest.setProductId(1L);
        addItemRequest.setQuantity(1);
        addItemRequest.setSize("M");

        // Mock ProductService to return the test product
        when(productService.findProductById(addItemRequest.getProductId())).thenReturn(product);

        // Mock CartRepository to return the user's cart
        when(cartRepository.findByUserId(user.getId())).thenReturn(cart);

        // Mock CartItemService to simulate that the item already exists in the cart
        when(cartItemService.isCartItemExist(cart, product, addItemRequest.getSize(), user.getId())).thenReturn(cartItem);

        // Call the service method to add the cart item
        CartItem addedCartItem = cartService.addCartItem(user.getId(), addItemRequest);

        // Assert that the existing cart item is returned and not a new one
        assertNotNull(addedCartItem);
        assertEquals(1, addedCartItem.getQuantity());  // Quantity should remain the same (since it already exists)
        
        // Verify the interactions with CartItemService
        verify(cartItemService, times(1)).isCartItemExist(cart, product, addItemRequest.getSize(), user.getId());
        verify(cartItemService, times(0)).createCartItem(any(CartItem.class));  // Ensure no new cart item is created
    }
}
