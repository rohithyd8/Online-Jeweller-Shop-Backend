package com.jewelleryshop.service;

import com.jewelleryshop.modal.OrderItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemServiceImplementationTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImplementation orderItemService;

    private OrderItem orderItem;
    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dynamically creating a Product object which might be returned from other services or repositories
        product = new Product();
        product.setId(1L);
        product.setTitle("Sample Product");
        product.setPrice(100);
        product.setDiscountedPrice(90);

        // Dynamically creating an OrderItem object
        orderItem = new OrderItem();
        orderItem.setProduct(product);  // Setting product dynamically
        orderItem.setSize("M");
        orderItem.setQuantity(3);  // Dynamic quantity
        orderItem.setPrice(product.getPrice());
        orderItem.setDiscountedPrice(product.getDiscountedPrice());
        orderItem.setUserId(123L);  // This could be dynamically set based on the user
    }

    @Test
    public void testCreateOrderItem_Success() {
        // Mock the save method of the repository to return the same orderItem
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        // Call the service method to create the OrderItem
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);

        // Assertions based on dynamically set data
        assertNotNull(createdOrderItem);
        assertEquals(3, createdOrderItem.getQuantity());  // Dynamically set quantity
        assertEquals("M", createdOrderItem.getSize());    // Dynamically set size
        assertEquals(100, createdOrderItem.getPrice());   // Product price set dynamically
        assertEquals(90, createdOrderItem.getDiscountedPrice());  // Product discounted price set dynamically
        assertEquals(123L, createdOrderItem.getUserId());  // Dynamically set user ID

        // Verify the interaction with the repository
        verify(orderItemRepository, times(1)).save(orderItem);
    }

    @Test
    public void testCreateOrderItem_Fail() {
        // Simulate a failure by returning null (or any other failure mechanism)
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(null);

        // Call the service method to create the OrderItem
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);

        // Assertions
        assertNull(createdOrderItem);  // If save fails, the returned value should be null

        // Verify the repository interaction
        verify(orderItemRepository, times(1)).save(orderItem);
    }
}
