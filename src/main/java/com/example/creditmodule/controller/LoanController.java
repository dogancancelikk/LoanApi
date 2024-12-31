package com.example.creditmodule.controller;

import com.example.creditmodule.entity.LoanInstallmentEntity;
import com.example.creditmodule.request.CreateLoanRequest;
import com.example.creditmodule.request.PayLoanRequest;
import com.example.creditmodule.response.CreateLoanResponse;
import com.example.creditmodule.response.LoanResponse;
import com.example.creditmodule.response.PaymentResponse;
import com.example.creditmodule.security.JwtService;
import com.example.creditmodule.service.LoanService;
import com.example.creditmodule.util.ApiEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.creditmodule.util.ApiEndpoints.CUSTOMER;

@RestController
@RequestMapping(CUSTOMER + ApiEndpoints.ID + ApiEndpoints.LOAN)
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Operations related to loan management for a specific customer.")
@SecurityRequirement(name = "BearerAuth")
public class LoanController {

    private final LoanService loanService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Create a new loan",
            description = "Creates a new loan for the specified customer. The user must have either ROLE_CUSTOMER (and match the ID) or ROLE_ADMIN to use this endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan data"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<CreateLoanResponse> createLoan(
            @PathVariable("id") UUID customerId,
            @Valid @RequestBody CreateLoanRequest request,
            HttpServletRequest httpServletRequest) {
        jwtService.validateAuthorization(httpServletRequest, customerId);
        return ResponseEntity.status(201).body(loanService.createLoan(customerId, request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "List loans",
            description = "Retrieves a paginated list of loans for the specified customer, with optional filtering and sorting."
    )
    public Page<LoanResponse> listLoans(
            @PathVariable("id") UUID customerId,
            @RequestParam(value = "numberOfInstallments", required = false) Integer numberOfInstallments,
            @RequestParam(value = "isPaid", required = false) Boolean isPaid,
            @RequestParam(value = "sort", defaultValue = "createDate") String sortBy,
            @RequestParam(value = "order", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest httpServletRequest
    ) {
        jwtService.validateAuthorization(httpServletRequest, customerId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        return loanService.listLoans(customerId, numberOfInstallments, isPaid, pageable);
    }

    @GetMapping(ApiEndpoints.LOAN_ID + ApiEndpoints.INSTALLMENTS)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "List loan installments",
            description = "Retrieves the installments for a specific loan associated with the customer."
    )
    public ResponseEntity<List<LoanInstallmentEntity>> listInstallments(
            @PathVariable("id") UUID customerId,
            @PathVariable("loanId") UUID loanId,
            HttpServletRequest httpServletRequest) {
        jwtService.validateAuthorization(httpServletRequest, customerId);
        return ResponseEntity.ok(loanService.listInstallments(loanId));
    }

    @PutMapping(ApiEndpoints.LOAN_ID)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Pay a loan installment",
            description = "Makes a payment towards the specified loan. This can be a partial or full payment, depending on the amount sent."
    )
    public ResponseEntity<PaymentResponse> payLoan(
            @PathVariable("id") UUID customerId,
            @PathVariable("loanId") UUID loanId,
            @RequestBody PayLoanRequest request,
            HttpServletRequest httpServletRequest) {
        jwtService.validateAuthorization(httpServletRequest, customerId);
        PaymentResponse response = loanService.payLoan(loanId, request.getAmount());
        return ResponseEntity.ok(response);
    }
}
