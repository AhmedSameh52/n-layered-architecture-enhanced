package com.example.nlayered.events.producer;

import com.example.nlayered.events.model.CustomerRegisteredEvent;
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
public class CustomerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.customer-events}")
    private String topic;

    public void publishRegistered(CustomerRegisteredEvent event) {
        String key = event.getCustomerId().toString();
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish CustomerRegisteredEvent for customerId={}: {}",
                        event.getCustomerId(), ex.getMessage());
            } else {
                log.debug("Published CustomerRegisteredEvent for customerId={} to partition={}",
                        event.getCustomerId(),
                        result.getRecordMetadata().partition());
            }
        });
    }
}