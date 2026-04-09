package com.example.nlayered.events.model;

import com.example.nlayered.events.dto.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private EventMetadata metadata;
    private Long          orderId;
    private Long          customerId;
    private BigDecimal    totalAmount;
    private List<LineItem> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private Long   productId;
        private String productName;
        private int    quantity;
    }
}