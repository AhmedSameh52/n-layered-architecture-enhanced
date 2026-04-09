package com.example.nlayered.common.mapper;

import com.example.nlayered.controller.dto.CustomerResponse;
import com.example.nlayered.core.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "fullName", expression = "java(customer.fullName())")
    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
}