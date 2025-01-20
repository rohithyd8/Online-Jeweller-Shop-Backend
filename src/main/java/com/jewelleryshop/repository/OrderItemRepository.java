package com.jewelleryshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jewelleryshop.modal.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
