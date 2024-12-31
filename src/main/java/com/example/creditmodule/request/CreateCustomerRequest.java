package com.example.creditmodule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Request payload for creating a new customer.")
public class CreateCustomerRequest implements Serializable {
    @NotBlank(message = "Name is required.")
    @Schema(
            description = "First name of the customer.",
            example = "John"
    )
    private String name;
    @NotBlank
    private String surname;
    @NotNull(message = "Credit limit is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be zero or greater.")
    @Schema(
            description = "Credit limit assigned to the customer. Must be zero or greater.",
            example = "10000.00"
    )
    private BigDecimal creditLimit;
    @NotBlank(message = "Username is required.")
    @Schema(
            description = "Username for the customer's account.",
            example = "johndoe"
    )
    private String username;

    @NotBlank(message = "Password is required.")
    @Schema(
            description = "Password for the customer's account.",
            example = "SecurePassword123!"
    )
    private String password;
}
