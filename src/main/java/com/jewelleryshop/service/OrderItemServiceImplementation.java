package com.jewelleryshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.modal.OrderItem;
import com.jewelleryshop.repository.OrderItemRepository;

@Service
public class OrderItemServiceImplementation implements OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImplementation.class);
    
    private OrderItemRepository orderItemRepository;
    
    public OrderItemServiceImplementation(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        logger.info("Creating OrderItem with Product ID: {} and Quantity: {}", orderItem.getProduct().getId(), orderItem.getQuantity());

        // Save the order item
        OrderItem createdOrderItem = orderItemRepository.save(orderItem);

        logger.info("Successfully created OrderItem with ID: {}", createdOrderItem.getId());

        return createdOrderItem;
    }
}
