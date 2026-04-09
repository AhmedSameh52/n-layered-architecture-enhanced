package com.example.nlayered.controller.dto;

import com.example.nlayered.common.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long           id;
    private String         firstName;
    private String         lastName;
    private String         fullName;
    private String         email;
    private String         phone;
    private Long           departmentId;
    private Long           roleId;
    private BigDecimal     salary;
    private LocalDate      hireDate;
    private EmployeeStatus status;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}