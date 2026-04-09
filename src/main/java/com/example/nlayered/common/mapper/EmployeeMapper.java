package com.example.nlayered.common.mapper;

import com.example.nlayered.controller.dto.EmployeeResponse;
import com.example.nlayered.core.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "fullName", expression = "java(employee.fullName())")
    EmployeeResponse toResponse(Employee employee);

    List<EmployeeResponse> toResponseList(List<Employee> employees);
}