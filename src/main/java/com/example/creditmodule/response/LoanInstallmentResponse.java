package com.example.creditmodule.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanInstallmentResponse implements Serializable {
    private UUID id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private UUID loanId;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private boolean isPaid;
}
