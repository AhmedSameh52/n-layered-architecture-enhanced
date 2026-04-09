package com.example.nlayered.events.producer;

import com.example.nlayered.events.model.OrderCreatedEvent;
import com.example.nlayered.events.model.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String topic;

    public void publishOrderCreated(OrderCreatedEvent event) {
        send(event.getOrderId().toString(), event, "OrderCreatedEvent");
    }

    public void publishStatusChanged(OrderStatusChangedEvent event) {
        send(event.getOrderId().toString(), event, "OrderStatusChangedEvent");
    }

    private void send(String key, Object payload, String eventName) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish {} [key={}]: {}", eventName, key, ex.getMessage());
            } else {
                log.debug("Published {} [key={}] partition={} offset={}",
                        eventName, key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}