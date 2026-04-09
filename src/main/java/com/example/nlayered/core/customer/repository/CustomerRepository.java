package com.example.nlayered.core.customer.repository;

import com.example.nlayered.common.enums.CustomerStatus;
import com.example.nlayered.core.customer.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll(int offset, int limit);

    List<Customer> findByStatus(CustomerStatus status, int offset, int limit);

    List<Customer> searchByName(String namePart, int offset, int limit);

    Customer save(Customer customer);

    Customer update(Customer customer);

    void deleteById(Long id);

    long count();

    long countByStatus(CustomerStatus status);

    boolean existsByEmail(String email);
}