package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
public class LoanEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false, insertable = false)
    private CustomerEntity customer;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "number_of_installment", nullable = false)
    private Integer numberOfInstallment;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = Boolean.FALSE;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "loan_id")
    private List<LoanInstallmentEntity> installments;
}
