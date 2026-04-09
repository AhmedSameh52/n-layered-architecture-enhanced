package com.example.nlayered.common.mapper;

import com.example.nlayered.controller.dto.OrderItemResponse;
import com.example.nlayered.controller.dto.OrderResponse;
import com.example.nlayered.core.order.entity.Order;
import com.example.nlayered.core.order.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem item);

    List<OrderItemResponse> toItemResponseList(List<OrderItem> items);

    List<OrderResponse> toResponseList(List<Order> orders);
}