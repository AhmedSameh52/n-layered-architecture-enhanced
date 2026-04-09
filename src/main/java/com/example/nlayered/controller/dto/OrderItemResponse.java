package com.example.nlayered.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long       id;
    private Long       productId;
    private String     productName;
    private int        quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}