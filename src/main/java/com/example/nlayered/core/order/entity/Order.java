package com.example.nlayered.core.order.entity;

import com.example.nlayered.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long          id;
    private Long          customerId;
    private Long          processedByEmployeeId;
    private OrderStatus   status;
    private BigDecimal    totalAmount;
    private String        notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Populated on fetch — not stored in orders table
    private List<OrderItem> items;

    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
}