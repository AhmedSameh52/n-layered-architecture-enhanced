package com.example.nlayered.core.order.repository;

import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.core.order.entity.Order;
import com.example.nlayered.core.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(Long id);

    List<Order> findByCustomerId(Long customerId, int offset, int limit);

    List<Order> findByStatus(OrderStatus status, int offset, int limit);

    List<Order> findByEmployeeId(Long employeeId, int offset, int limit);

    Order save(Order order);

    void saveItems(List<OrderItem> items);

    Order updateStatus(Long orderId, OrderStatus status);

    List<OrderItem> findItemsByOrderId(Long orderId);

    long countByCustomerId(Long customerId);

    long countByStatus(OrderStatus status);
}