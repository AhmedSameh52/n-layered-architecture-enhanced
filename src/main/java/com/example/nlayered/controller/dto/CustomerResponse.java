package com.example.nlayered.controller.dto;

import com.example.nlayered.common.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long           id;
    private String         firstName;
    private String         lastName;
    private String         fullName;
    private String         email;
    private String         phone;
    private String         address;
    private CustomerStatus status;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}