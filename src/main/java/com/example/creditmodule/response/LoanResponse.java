package com.example.creditmodule.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LoanResponse implements Serializable {
    private UUID id;
    private UUID customerId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallment;
    private LocalDate createDate;
    private boolean isPaid;
    private List<LoanInstallmentResponse> installments;
}