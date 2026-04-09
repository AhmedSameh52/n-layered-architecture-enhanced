package com.example.nlayered.controller;

import com.example.nlayered.controller.dto.ApiResponse;
import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.controller.dto.CreateOrderRequest;
import com.example.nlayered.controller.dto.OrderResponse;
import com.example.nlayered.core.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getOrder(id));
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<PagedResponse<OrderResponse>> listByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(orderService.listByCustomer(customerId, page, size));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<PagedResponse<OrderResponse>> listByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(orderService.listByStatus(status, page, size));
    }

    @PostMapping("/{id}/advance")
    public ApiResponse<OrderResponse> advance(@PathVariable Long id) {
        return ApiResponse.ok("Order status advanced", orderService.advanceStatus(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok("Order cancelled", orderService.cancelOrder(id));
    }

    @PatchMapping("/{id}/assign-employee/{employeeId}")
    public ApiResponse<OrderResponse> assignEmployee(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        return ApiResponse.ok(orderService.assignEmployee(id, employeeId));
    }
}