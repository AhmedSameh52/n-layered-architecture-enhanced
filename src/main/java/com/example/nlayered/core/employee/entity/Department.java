package com.example.nlayered.core.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private Long          id;
    private String        name;
    private String        description;
    private Long          managerEmployeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}