package com.example.nlayered.core.employee.service.impl;

import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.EmployeeStatus;
import com.example.nlayered.common.mapper.EmployeeMapper;
import com.example.nlayered.controller.dto.CreateEmployeeRequest;
import com.example.nlayered.controller.dto.EmployeeResponse;
import com.example.nlayered.controller.dto.UpdateEmployeeRequest;
import com.example.nlayered.core.employee.entity.Employee;
import com.example.nlayered.core.employee.repository.DepartmentRepository;
import com.example.nlayered.core.employee.repository.EmployeeRepository;
import com.example.nlayered.core.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final BigDecimal MAX_SALARY = new BigDecimal("500000.00");

    private final EmployeeRepository   employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper       employeeMapper;

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An employee with email '%s' already exists".formatted(request.getEmail()));
        }
        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new IllegalArgumentException("Department not found: " + request.getDepartmentId());
        }
        if (request.getSalary().compareTo(MAX_SALARY) > 0) {
            throw new IllegalArgumentException(
                    "Salary %.2f exceeds the maximum allowed %.2f"
                            .formatted(request.getSalary(), MAX_SALARY));
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .departmentId(request.getDepartmentId())
                .roleId(request.getRoleId())
                .salary(request.getSalary())
                .hireDate(request.getHireDate())
                .status(EmployeeStatus.PROBATION)
                .build();

        employee = employeeRepository.save(employee);
        log.info("Created employee id={} department={}", employee.getId(), employee.getDepartmentId());

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployee(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    @Override
    public PagedResponse<EmployeeResponse> listEmployees(int page, int size) {
        int offset = page * size;
        List<Employee> employees = employeeRepository.findAll(offset, size);
        long total = employeeRepository.count();
        return PagedResponse.of(employeeMapper.toResponseList(employees), page, size, total);
    }

    @Override
    public PagedResponse<EmployeeResponse> listByDepartment(Long departmentId, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("Department not found: " + departmentId);
        }
        int offset = page * size;
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId, offset, size);
        long total = employeeRepository.countByDepartmentId(departmentId);
        return PagedResponse.of(employeeMapper.toResponseList(employees), page, size, total);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));

        if (request.getDepartmentId() != null
                && !departmentRepository.existsById(request.getDepartmentId())) {
            throw new IllegalArgumentException("Department not found: " + request.getDepartmentId());
        }

        Employee updated = existing.toBuilder()
                .firstName(request.getFirstName()    != null ? request.getFirstName()    : existing.getFirstName())
                .lastName(request.getLastName()      != null ? request.getLastName()     : existing.getLastName())
                .phone(request.getPhone()            != null ? request.getPhone()        : existing.getPhone())
                .departmentId(request.getDepartmentId() != null ? request.getDepartmentId() : existing.getDepartmentId())
                .roleId(request.getRoleId()          != null ? request.getRoleId()       : existing.getRoleId())
                .salary(request.getSalary()          != null ? request.getSalary()       : existing.getSalary())
                .build();

        return employeeMapper.toResponse(employeeRepository.update(updated));
    }

    @Override
    public EmployeeResponse updateStatus(Long id, EmployeeStatus status) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Employee not found: " + id);
        }
        employeeRepository.updateStatus(id, status);
        log.info("Updated employee id={} status → {}", id, status);
        return getEmployee(id);
    }

    @Override
    public void terminateEmployee(Long id) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
        employeeRepository.updateStatus(id, EmployeeStatus.TERMINATED);
        log.info("Terminated employee id={}", id);
    }

    @Override
    public void assignToDepartment(Long employeeId, Long departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("Department not found: " + departmentId);
        }
        employeeRepository.update(employee.toBuilder().departmentId(departmentId).build());
        log.info("Assigned employee id={} to department id={}", employeeId, departmentId);
    }
}