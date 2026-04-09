package com.example.nlayered.events.model;

import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.events.dto.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {

    private EventMetadata metadata;
    private Long          orderId;
    private Long          customerId;
    private OrderStatus   previousStatus;
    private OrderStatus   newStatus;
    private String        reason;
}