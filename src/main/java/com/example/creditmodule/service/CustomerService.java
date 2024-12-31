package com.example.creditmodule.service;

import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.exception.ApiException;
import com.example.creditmodule.mapper.CustomerMapper;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.repository.UserRepository;
import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        UserEntity customerUser = userService.createCustomerUser(request.getUsername(), request.getPassword());
        CustomerEntity newCustomer = customerMapper.toEntity(request);
        newCustomer.setId(UUID.randomUUID());
        newCustomer.setUser(customerUser);
        customerRepository.save(newCustomer);
        return customerMapper.toCustomerResponse(newCustomer);

    }

    public List<CustomerResponse> getAllCustomers() {
        List<CustomerEntity> allCustomers = customerRepository.findAll();
        return allCustomers.isEmpty() ? Collections.emptyList() : customerMapper.mapAll(allCustomers);
    }

    public CustomerResponse getCustomer(UUID customerId) {
        CustomerEntity customerEntity = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
        return customerMapper.toCustomerResponse(customerEntity);
    }
}
