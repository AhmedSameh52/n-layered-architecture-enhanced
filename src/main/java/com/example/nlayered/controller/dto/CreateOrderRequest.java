package com.example.nlayered.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull
    private Long customerId;

    private Long processedByEmployeeId;

    private String notes;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {

        @NotNull
        private Long productId;

        @NotBlank
        private String productName;

        @Min(1)
        private int quantity;

        @NotNull
        @DecimalMin("0.01")
        private BigDecimal unitPrice;
    }
}