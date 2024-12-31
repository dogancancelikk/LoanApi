package com.example.creditmodule.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class CreateLoanRequest implements Serializable {

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    @Schema(
            description = "The principal loan amount. Must be greater than zero.",
            example = "5000"
    )
    private BigDecimal amount;
    @NotNull(message = "Interest rate is required.")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1.")
    @DecimalMax(value = "0.5", message = "Interest rate must not exceed 0.5.")
    @Schema(
            description = "Monthly interest rate. Must be between 0.1 and 0.5 (inclusive).",
            example = "0.15"
    )
    private BigDecimal interestRate;
    @Min(6)
    @Max(24)
    @Schema(
            description = "Number of installments for the loan. Must be 6,9,12 or 24.",
            example = "12"
    )
    private Integer numberOfInstallments;

}
