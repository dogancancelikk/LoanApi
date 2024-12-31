package com.example.creditmodule.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PaymentResponse implements Serializable {
    Integer countOfPaidInstallments;
    BigDecimal totalAmount;
    Boolean isLoanFullyPaid;
    List<LoanInstallmentResponse> remainingInstallments;
}
