package com.example.nlayered.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadata {

    private String  eventId;
    private String  eventType;
    private String  source;
    private Instant occurredAt;
    private String  correlationId;

    public static EventMetadata of(String eventType) {
        return EventMetadata.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .source("nlayered-service")
                .occurredAt(Instant.now())
                .build();
    }

    public static EventMetadata of(String eventType, String correlationId) {
        return EventMetadata.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .source("nlayered-service")
                .occurredAt(Instant.now())
                .correlationId(correlationId)
                .build();
    }
}