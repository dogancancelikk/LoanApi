package com.example.creditmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @Column(name = "customer_id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "used_credit_limit", nullable = false)
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    UserEntity user;
}
