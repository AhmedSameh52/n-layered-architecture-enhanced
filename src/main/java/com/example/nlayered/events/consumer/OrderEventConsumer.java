package com.example.nlayered.events.consumer;

import com.example.nlayered.events.model.OrderCreatedEvent;
import com.example.nlayered.events.model.OrderStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics    = "${kafka.topics.order-events}",
            groupId   = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderEvent(
            @Payload Object rawEvent,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        switch (rawEvent) {
            case OrderCreatedEvent e       -> handleOrderCreated(e, partition, offset);
            case OrderStatusChangedEvent e -> handleStatusChanged(e, partition, offset);
            default -> log.warn("Unrecognised order event type: {}", rawEvent.getClass().getName());
        }
    }

    private void handleOrderCreated(OrderCreatedEvent event, int partition, long offset) {
        log.info("Order created [orderId={}, customerId={}, items={}, partition={}, offset={}]",
                event.getOrderId(),
                event.getCustomerId(),
                event.getItems().size(),
                partition,
                offset);
        // Downstream: trigger fulfilment pipeline, update dashboards, etc.
    }

    private void handleStatusChanged(OrderStatusChangedEvent event, int partition, long offset) {
        log.info("Order status changed [orderId={}, {} → {}, partition={}, offset={}]",
                event.getOrderId(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                partition,
                offset);
        // Downstream: notify customer, trigger shipping, process refund, etc.
    }
}