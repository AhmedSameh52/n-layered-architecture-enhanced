package com.example.nlayered.core.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long          id;
    private Long          orderId;
    private Long          productId;
    private String        productName;
    private int           quantity;
    private BigDecimal    unitPrice;
    private BigDecimal    totalPrice;
    private LocalDateTime createdAt;
}