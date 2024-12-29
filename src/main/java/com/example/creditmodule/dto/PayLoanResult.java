package com.example.creditmodule.dto;

import java.math.BigDecimal;

public record PayLoanResult(
        int installmentsPaid,
        BigDecimal totalSpent,
        boolean loanFullyPaid,
        String message
) {}
