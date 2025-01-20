package com.jewelleryshop.service;


import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.modal.*;
import com.jewelleryshop.repository.AddressRepository;
import com.jewelleryshop.repository.OrderItemRepository;
import com.jewelleryshop.repository.OrderRepository;
import com.jewelleryshop.repository.UserRepository;
import com.jewelleryshop.user.domain.OrderStatus;
import com.jewelleryshop.user.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceImplementationTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User user;
    private Address address;
    private Cart cart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize User
        user = new User();
        user.setId(1L);
        user.setAddresses(new ArrayList<>());
        
        // Initialize Address
        address = new Address();
        address.setId(1L);
        address.setUser(user);

        // Initialize Cart with dynamic values
        cart = new Cart();
        cart.setTotalPrice(500.0);
        cart.setTotalDiscountedPrice(400);
        cart.setDiscounte(10);
        cart.setTotalItem(3);

        CartItem cartItem1 = new CartItem();
        cartItem1.setPrice(100);
        cartItem1.setQuantity(2);
        cartItem1.setDiscountedPrice(80);

        CartItem cartItem2 = new CartItem();
        cartItem2.setPrice(200);
        cartItem2.setQuantity(1);
        cartItem2.setDiscountedPrice(180);

        cart.setCartItems(Arrays.asList(cartItem1, cartItem2));  // Adding 2 cart items dynamically
    }

    @Test
    public void testCreateOrder_Success() {
        // Mock cart retrieval
        when(cartService.findUserCart(user.getId())).thenReturn(cart);

        // Mock address saving
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Mock order item saving (dynamically saving based on cart items)
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock order saving
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call createOrder method
        Order createdOrder = orderService.createOrder(user, address);

        // Assertions based on the actual service logic
        assertNotNull(createdOrder);
        assertEquals(500.0, createdOrder.getTotalPrice());  // Total price from cart
        assertEquals(400, createdOrder.getTotalDiscountedPrice());  // Discounted price from cart
        assertEquals(3, createdOrder.getTotalItem());  // Total items from cart
        assertEquals(OrderStatus.PENDING, createdOrder.getOrderStatus());  // Status should be PENDING initially
    }

    @Test
    public void testCreateOrder_UserNotFound() {
        // Simulate user not found scenario
        when(cartService.findUserCart(user.getId())).thenThrow(new RuntimeException("User not found"));

        // Call createOrder method and assert exception
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(user, address);
        });
    }

    @Test
    public void testPlacedOrder_Success() throws OrderException {
        // Mock findOrderById
        Order existingOrder = new Order();
        existingOrder.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        // Call placedOrder method
        Order placedOrder = orderService.placedOrder(1L);

        // Assertions based on service logic
        assertEquals(OrderStatus.PLACED, placedOrder.getOrderStatus());  // Order status should be PLACED
        assertEquals(PaymentStatus.COMPLETED, placedOrder.getPaymentDetails().getStatus());  // Payment should be completed
    }

    @Test
    public void testPlacedOrder_OrderNotFound() throws OrderException {
        // Mock findOrderById to return empty
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Call placedOrder method and assert exception
        assertThrows(OrderException.class, () -> {
            orderService.placedOrder(1L);
        });
    }

    @Test
    public void testConfirmedOrder_Success() throws OrderException {
        // Mock findOrderById
        Order existingOrder = new Order();
        existingOrder.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        // Mock order saving
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call confirmedOrder method
        Order confirmedOrder = orderService.confirmedOrder(1L);

        // Assertions based on service logic
        assertEquals(OrderStatus.CONFIRMED, confirmedOrder.getOrderStatus());  // Order status should be CONFIRMED
    }

    @Test
    public void testConfirmedOrder_OrderNotFound() throws OrderException {
        // Mock findOrderById to return empty
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Call confirmedOrder method and assert exception
        assertThrows(OrderException.class, () -> {
            orderService.confirmedOrder(1L);
        });
    }

    @Test
    public void testDeleteOrder_Success() throws OrderException {
        // Mock findOrderById
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        // Call deleteOrder method
        orderService.deleteOrder(1L);

        // Verify delete method is called
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOrder_OrderNotFound() throws OrderException {
        // Mock findOrderById to return empty
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Call deleteOrder method and assert exception
        assertThrows(OrderException.class, () -> {
            orderService.deleteOrder(1L);
        });
    }

    @Test
    public void testGetAllOrders() {
        // Mocking the order repository
        List<Order> orderList = Arrays.asList(new Order(), new Order(), new Order());
        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(orderList);

        // Call getAllOrders method
        List<Order> orders = orderService.getAllOrders();

        // Assertions based on dynamic return values
        assertNotNull(orders);
        assertEquals(3, orders.size());
    }

    @Test
    public void testUsersOrderHistory() {
        // Mocking user orders
        List<Order> userOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.getUsersOrders(1L)).thenReturn(userOrders);

        // Call usersOrderHistory method
        List<Order> orders = orderService.usersOrderHistory(1L);

        // Assertions based on dynamic return values
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }
}

