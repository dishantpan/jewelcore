package com.dishant.jewelcore.billing.controller;

import com.dishant.jewelcore.billing.dto.CustomerResponse;
import com.dishant.jewelcore.billing.entity.Customer;
import com.dishant.jewelcore.billing.repository.CustomerRepository;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody Customer customer) {
        if (customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists: " + customer.getPhone());
        }
        if (customer.getEmail() != null && customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created", toResponse(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<Customer> customers = customerRepository.findByActiveTrueOrderByCreatedAtDesc();
        List<CustomerResponse> responses = customers.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Customers fetched", responses));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(@RequestParam String q) {
        List<Customer> customers = customerRepository.search(q);
        List<CustomerResponse> responses = customers.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Search results", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return ResponseEntity.ok(ApiResponse.success("Customer fetched", toResponse(customer)));
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setAddress(customer.getAddress());
        response.setGstNumber(customer.getGstNumber());
        response.setActive(customer.getActive());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
}