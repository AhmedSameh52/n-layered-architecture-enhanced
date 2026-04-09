package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.EmailRequest;
import com.example.nlayered.common.clients.dto.NotificationResponse;

public interface NotificationClient {

    NotificationResponse sendEmail(EmailRequest request);

    NotificationResponse sendSms(EmailRequest request);
}