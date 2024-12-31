package com.example.creditmodule.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateLoanResponse implements Serializable {
    UUID loanId;
    BigDecimal loanAmount;
    BigDecimal totalPayment;
    BigDecimal installmentAmount;
    Integer numberOfInstallments;
}
