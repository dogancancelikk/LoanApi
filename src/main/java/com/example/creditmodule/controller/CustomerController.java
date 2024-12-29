package com.example.creditmodule.controller;


import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.CustomerResponse;
import com.example.creditmodule.security.JwtService;
import com.example.creditmodule.service.CustomerService;
import com.example.creditmodule.util.ApiEndpoints;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.CUSTOMER)
public class CustomerController {
    private final JwtService jwtService;
    private final CustomerService customerService;

    @GetMapping(ApiEndpoints.ID)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomerResponse> getCustomerInfo(
            @PathVariable("id") UUID customerId,
            HttpServletRequest request) {
        jwtService.validateAuthorization(request, customerId);
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request) {
        CustomerResponse customer = customerService.createCustomer(request);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(customer.getId())
                        .toUri())
                .body(customer);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

}
