package com.example.nlayered.controller;

import com.example.nlayered.controller.dto.ApiResponse;
import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.CustomerStatus;
import com.example.nlayered.controller.dto.CreateCustomerRequest;
import com.example.nlayered.controller.dto.CustomerResponse;
import com.example.nlayered.controller.dto.UpdateCustomerRequest;
import com.example.nlayered.core.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer created successfully", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(customerService.getCustomer(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<CustomerResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(customerService.listCustomers(page, size));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<PagedResponse<CustomerResponse>> listByStatus(
            @PathVariable CustomerStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(customerService.listByStatus(status, page, size));
    }

    @GetMapping("/search")
    public ApiResponse<List<CustomerResponse>> search(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(customerService.searchByName(name, page, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ApiResponse.ok("Customer updated", customerService.updateCustomer(id, request));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable Long id) {
        customerService.suspendCustomer(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}