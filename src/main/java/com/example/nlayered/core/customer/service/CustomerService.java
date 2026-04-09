package com.example.nlayered.core.customer.service;

import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.CustomerStatus;
import com.example.nlayered.controller.dto.CreateCustomerRequest;
import com.example.nlayered.controller.dto.CustomerResponse;
import com.example.nlayered.controller.dto.UpdateCustomerRequest;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomer(Long id);

    PagedResponse<CustomerResponse> listCustomers(int page, int size);

    PagedResponse<CustomerResponse> listByStatus(CustomerStatus status, int page, int size);

    List<CustomerResponse> searchByName(String name, int page, int size);

    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);

    void suspendCustomer(Long id);

    void deleteCustomer(Long id);
}