package com.example.nlayered.controller.dto;

import com.example.nlayered.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long                  id;
    private Long                  customerId;
    private Long                  processedByEmployeeId;
    private OrderStatus           status;
    private BigDecimal            totalAmount;
    private String                notes;
    private List<OrderItemResponse> items;
    private LocalDateTime         createdAt;
    private LocalDateTime         updatedAt;
}