package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.EmailRequest;
import com.example.nlayered.common.clients.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Demo stub — logs calls and returns synthetic responses.
 * Replace with a real HTTP implementation (RestClient, WebClient, etc.)
 * when a live notification service is available.
 */
@Slf4j
@Component
public class NotificationClientStub implements NotificationClient {

    @Override
    public NotificationResponse sendEmail(EmailRequest request) {
        log.info("STUB sendEmail — to={} subject={} template={}",
                request.getTo(), request.getSubject(), request.getTemplateId());
        return new NotificationResponse(UUID.randomUUID().toString(), "SENT");
    }

    @Override
    public NotificationResponse sendSms(EmailRequest request) {
        log.info("STUB sendSms — to={}", request.getTo());
        return new NotificationResponse(UUID.randomUUID().toString(), "SENT");
    }
}