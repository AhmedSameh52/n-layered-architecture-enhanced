package com.example.nlayered.core.order.service;

import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.controller.dto.CreateOrderRequest;
import com.example.nlayered.controller.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(Long id);

    PagedResponse<OrderResponse> listByCustomer(Long customerId, int page, int size);

    PagedResponse<OrderResponse> listByStatus(OrderStatus status, int page, int size);

    OrderResponse advanceStatus(Long orderId);

    OrderResponse cancelOrder(Long orderId);

    OrderResponse assignEmployee(Long orderId, Long employeeId);
}