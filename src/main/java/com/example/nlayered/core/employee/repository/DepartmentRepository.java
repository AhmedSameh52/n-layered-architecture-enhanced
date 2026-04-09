package com.example.nlayered.core.employee.repository;

import com.example.nlayered.core.employee.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    Optional<Department> findById(Long id);

    Optional<Department> findByName(String name);

    List<Department> findAll();

    Department save(Department department);

    Department update(Department department);

    void assignManager(Long departmentId, Long employeeId);

    boolean existsById(Long id);

    boolean existsByName(String name);
}