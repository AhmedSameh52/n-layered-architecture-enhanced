package com.example.nlayered.events.consumer;

import com.example.nlayered.events.model.CustomerRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerEventConsumer {

    /**
     * Listens for customer-registered events and triggers downstream workflows —
     * e.g. initialising a loyalty-points account or dispatching a welcome email
     * via an internal pipeline (not via the Feign client, which is used by the service layer).
     */
    @KafkaListener(
            topics    = "${kafka.topics.customer-events}",
            groupId   = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCustomerRegistered(
            @Payload CustomerRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received CustomerRegisteredEvent [eventId={}, customerId={}, partition={}, offset={}]",
                event.getMetadata().getEventId(),
                event.getCustomerId(),
                partition,
                offset);

        initLoyaltyAccount(event);
    }

    private void initLoyaltyAccount(CustomerRegisteredEvent event) {
        // Downstream: create a zero-balance loyalty record for the new customer.
        log.debug("Initialising loyalty account for customerId={}", event.getCustomerId());
    }
}