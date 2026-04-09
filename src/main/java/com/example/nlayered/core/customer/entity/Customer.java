package com.example.nlayered.core.customer.entity;

import com.example.nlayered.common.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long           id;
    private String         firstName;
    private String         lastName;
    private String         email;
    private String         phone;
    private String         address;
    private CustomerStatus status;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;

    public String fullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }
}