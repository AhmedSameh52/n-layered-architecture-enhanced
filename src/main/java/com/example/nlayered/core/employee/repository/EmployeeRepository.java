package com.example.nlayered.core.employee.repository;

import com.example.nlayered.common.enums.EmployeeStatus;
import com.example.nlayered.core.employee.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Optional<Employee> findById(Long id);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAll(int offset, int limit);

    List<Employee> findByDepartmentId(Long departmentId, int offset, int limit);

    List<Employee> findByRoleId(Long roleId);

    List<Employee> findByStatus(EmployeeStatus status, int offset, int limit);

    Employee save(Employee employee);

    Employee update(Employee employee);

    void updateStatus(Long id, EmployeeStatus status);

    long count();

    long countByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);
}