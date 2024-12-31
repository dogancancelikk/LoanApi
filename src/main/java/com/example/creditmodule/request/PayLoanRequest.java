package com.example.creditmodule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PayLoanRequest {
    @NotNull(message = "Loan ID is required.")
    @Schema(
            description = "Unique identifier of the loan being paid.",
            example = "12345"
    )
    private UUID loanId;

    @NotNull(message = "Payment amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than zero.")
    @Schema(
            description = "The amount to be paid towards the loan. Must be greater than zero.",
            example = "500.00"
    )
    private BigDecimal amount;
}
