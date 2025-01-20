package com.jewelleryshop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.modal.Address;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.modal.OrderItem;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.AddressRepository;
import com.jewelleryshop.repository.OrderItemRepository;
import com.jewelleryshop.repository.OrderRepository;
import com.jewelleryshop.repository.UserRepository;
import com.jewelleryshop.user.domain.OrderStatus;
import com.jewelleryshop.user.domain.PaymentStatus;

@Service
public class OrderServiceImplementation implements OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImplementation.class);
    
    private OrderRepository orderRepository;
    private CartService cartService;
    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private OrderItemService orderItemService;
    private OrderItemRepository orderItemRepository;
    
    public OrderServiceImplementation(OrderRepository orderRepository, CartService cartService,
                                      AddressRepository addressRepository, UserRepository userRepository,
                                      OrderItemService orderItemService, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Order createOrder(User user, Address shippingAddress) {
        logger.info("Creating order for user: {}", user.getId());
        
        shippingAddress.setUser(user);
        Address address = addressRepository.save(shippingAddress);
        user.getAddresses().add(address);
        userRepository.save(user);
        
        Cart cart = cartService.findUserCart(user.getId());
        List<OrderItem> orderItems = new ArrayList<>();
        
        logger.info("Processing cart items for order creation.");
        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(item.getPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setDiscountedPrice(item.getDiscountedPrice());
            
            OrderItem createdOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(createdOrderItem);
        }
        
        Order createdOrder = new Order();
        createdOrder.setUser(user);
        createdOrder.setOrderItems(orderItems);
        createdOrder.setTotalPrice(cart.getTotalPrice());
        createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
        createdOrder.setDiscounte(cart.getDiscounte());
        createdOrder.setTotalItem(cart.getTotalItem());
        createdOrder.setShippingAddress(address);
        createdOrder.setOrderDate(LocalDateTime.now());
        createdOrder.setOrderStatus(OrderStatus.PENDING);
        createdOrder.getPaymentDetails().setStatus(PaymentStatus.PENDING);
        createdOrder.setCreatedAt(LocalDateTime.now());
        
        logger.info("Saving the order to the repository.");
        Order savedOrder = orderRepository.save(createdOrder);
        
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public Order placedOrder(Long orderId) throws OrderException {
        logger.info("Marking order with ID: {} as placed.", orderId);
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.PLACED);
        order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
        return orderRepository.save(order);
    }

    @Override
    public Order confirmedOrder(Long orderId) throws OrderException {
        logger.info("Marking order with ID: {} as confirmed.", orderId);
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(Long orderId) throws OrderException {
        logger.info("Marking order with ID: {} as shipped.", orderId);
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    @Override
    public Order deliveredOrder(Long orderId) throws OrderException {
        logger.info("Marking order with ID: {} as delivered.", orderId);
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    public Order canceledOrder(Long orderId) throws OrderException {
        logger.info("Marking order with ID: {} as canceled.", orderId);
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {
        logger.info("Finding order with ID: {}", orderId);
        Optional<Order> opt = orderRepository.findById(orderId);
        
        if (opt.isPresent()) {
            return opt.get();
        }
        
        logger.error("Order not found with ID: {}", orderId);
        throw new OrderException("Order not found with id " + orderId);
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        logger.info("Fetching order history for user with ID: {}", userId);
        List<Order> orders = orderRepository.getUsersOrders(userId);
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders.");
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {
        logger.info("Deleting order with ID: {}", orderId);
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
    }

	@Override
	public Order cancledOrder(Long orderId) throws OrderException {
		// TODO Auto-generated method stub
		return null;
	}
}
