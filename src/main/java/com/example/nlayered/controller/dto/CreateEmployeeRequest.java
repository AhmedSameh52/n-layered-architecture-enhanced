package com.example.nlayered.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotNull
    private Long departmentId;

    @NotNull
    private Long roleId;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("9999999.99")
    private BigDecimal salary;

    @NotNull
    @PastOrPresent
    private LocalDate hireDate;
}