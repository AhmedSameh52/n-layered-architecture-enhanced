package com.example.nlayered.controller;

import com.example.nlayered.controller.dto.ApiResponse;
import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.EmployeeStatus;
import com.example.nlayered.controller.dto.CreateEmployeeRequest;
import com.example.nlayered.controller.dto.EmployeeResponse;
import com.example.nlayered.controller.dto.UpdateEmployeeRequest;
import com.example.nlayered.core.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee created successfully", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.getEmployee(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<EmployeeResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(employeeService.listEmployees(page, size));
    }

    @GetMapping("/department/{departmentId}")
    public ApiResponse<PagedResponse<EmployeeResponse>> listByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(employeeService.listByDepartment(departmentId, page, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ApiResponse.ok("Employee updated", employeeService.updateEmployee(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<EmployeeResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam EmployeeStatus status) {
        return ApiResponse.ok(employeeService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/department/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignDepartment(
            @PathVariable Long id,
            @PathVariable Long departmentId) {
        employeeService.assignToDepartment(id, departmentId);
    }

    @DeleteMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void terminate(@PathVariable Long id) {
        employeeService.terminateEmployee(id);
    }
}