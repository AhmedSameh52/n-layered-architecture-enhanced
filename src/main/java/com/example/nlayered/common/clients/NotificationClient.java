package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.EmailRequest;
import com.example.nlayered.common.clients.dto.NotificationResponse;
import com.example.nlayered.common.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service",
        url = "${services.notification.url}",
        configuration = OpenFeignConfig.class
)
public interface NotificationClient {

    @PostMapping("/api/notifications/email")
    NotificationResponse sendEmail(@RequestBody EmailRequest request);

    @PostMapping("/api/notifications/sms")
    NotificationResponse sendSms(@RequestBody EmailRequest request);
}