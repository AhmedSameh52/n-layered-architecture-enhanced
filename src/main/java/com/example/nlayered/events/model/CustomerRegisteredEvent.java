package com.example.nlayered.events.model;

import com.example.nlayered.events.dto.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisteredEvent {

    private EventMetadata metadata;
    private Long          customerId;
    private String        firstName;
    private String        lastName;
    private String        email;
    private String        phone;
}