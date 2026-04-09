package com.example.nlayered.common.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String messageId;
    private String status;
}