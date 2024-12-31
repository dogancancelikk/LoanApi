package com.example.creditmodule.controller;


import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.CustomerResponse;
import com.example.creditmodule.security.JwtService;
import com.example.creditmodule.service.CustomerService;
import com.example.creditmodule.util.ApiEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customers", description = "Operations related to customer management")
@SecurityRequirement(name = "BearerAuth")
public class CustomerController {
    private final JwtService jwtService;
    private final CustomerService customerService;

    @GetMapping(ApiEndpoints.ID)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves the information of a specific customer by their unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found and returned"),
            @ApiResponse(responseCode = "403", description = "Access denied for the given user"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> getCustomerInfo(
            @PathVariable("id") UUID customerId,
            HttpServletRequest request) {
        jwtService.validateAuthorization(request, customerId);
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer with the provided details. Only available to admin users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied for the current user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(
            summary = "List Customers",
            description = "It is authorized by admin users"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

}
