package com.example.nlayered.core.customer.service.impl;

import com.example.nlayered.common.clients.NotificationClient;
import com.example.nlayered.common.clients.dto.EmailRequest;
import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.CustomerStatus;
import com.example.nlayered.common.mapper.CustomerMapper;
import com.example.nlayered.controller.dto.CreateCustomerRequest;
import com.example.nlayered.controller.dto.CustomerResponse;
import com.example.nlayered.controller.dto.UpdateCustomerRequest;
import com.example.nlayered.core.customer.entity.Customer;
import com.example.nlayered.core.customer.repository.CustomerRepository;
import com.example.nlayered.core.customer.service.CustomerService;
import com.example.nlayered.events.dto.EventMetadata;
import com.example.nlayered.events.model.CustomerRegisteredEvent;
import com.example.nlayered.events.producer.CustomerEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository    customerRepository;
    private final CustomerMapper        customerMapper;
    private final CustomerEventProducer customerEventProducer;
    private final NotificationClient    notificationClient;

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "A customer with email '%s' already exists".formatted(request.getEmail()));
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(CustomerStatus.ACTIVE)
                .build();

        customer = customerRepository.save(customer);
        log.info("Created customer id={} email={}", customer.getId(), customer.getEmail());

        publishRegisteredEvent(customer);
        sendWelcomeEmail(customer);

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @Override
    public PagedResponse<CustomerResponse> listCustomers(int page, int size) {
        int offset = page * size;
        List<Customer> customers = customerRepository.findAll(offset, size);
        long total = customerRepository.count();
        return PagedResponse.of(customerMapper.toResponseList(customers), page, size, total);
    }

    @Override
    public PagedResponse<CustomerResponse> listByStatus(CustomerStatus status, int page, int size) {
        int offset = page * size;
        List<Customer> customers = customerRepository.findByStatus(status, offset, size);
        long total = customerRepository.countByStatus(status);
        return PagedResponse.of(customerMapper.toResponseList(customers), page, size, total);
    }

    @Override
    public List<CustomerResponse> searchByName(String name, int page, int size) {
        int offset = page * size;
        return customerMapper.toResponseList(customerRepository.searchByName(name, offset, size));
    }

    @Override
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        Customer updated = existing.toBuilder()
                .firstName(request.getFirstName() != null ? request.getFirstName() : existing.getFirstName())
                .lastName(request.getLastName()   != null ? request.getLastName()  : existing.getLastName())
                .phone(request.getPhone()         != null ? request.getPhone()     : existing.getPhone())
                .address(request.getAddress()     != null ? request.getAddress()   : existing.getAddress())
                .build();

        return customerMapper.toResponse(customerRepository.update(updated));
    }

    @Override
    public void suspendCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        if (customer.getStatus() == CustomerStatus.SUSPENDED) {
            return;
        }

        customerRepository.update(customer.toBuilder().status(CustomerStatus.SUSPENDED).build());
        log.info("Suspended customer id={}", id);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Soft-deleted customer id={}", id);
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private void publishRegisteredEvent(Customer customer) {
        CustomerRegisteredEvent event = CustomerRegisteredEvent.builder()
                .metadata(EventMetadata.of("CUSTOMER_REGISTERED"))
                .customerId(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
        customerEventProducer.publishRegistered(event);
    }

    private void sendWelcomeEmail(Customer customer) {
        try {
            notificationClient.sendEmail(EmailRequest.builder()
                    .to(customer.getEmail())
                    .subject("Welcome to our platform!")
                    .templateId("welcome-customer")
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to send welcome email to customerId={}: {}", customer.getId(), ex.getMessage());
        }
    }
}