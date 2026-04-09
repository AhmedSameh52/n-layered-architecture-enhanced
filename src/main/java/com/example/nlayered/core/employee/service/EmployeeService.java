package com.example.nlayered.core.employee.service;

import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.EmployeeStatus;
import com.example.nlayered.controller.dto.CreateEmployeeRequest;
import com.example.nlayered.controller.dto.EmployeeResponse;
import com.example.nlayered.controller.dto.UpdateEmployeeRequest;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse getEmployee(Long id);

    PagedResponse<EmployeeResponse> listEmployees(int page, int size);

    PagedResponse<EmployeeResponse> listByDepartment(Long departmentId, int page, int size);

    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);

    EmployeeResponse updateStatus(Long id, EmployeeStatus status);

    void terminateEmployee(Long id);

    void assignToDepartment(Long employeeId, Long departmentId);
}