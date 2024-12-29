package com.example.creditmodule.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CustomerResponse implements Serializable {
    private UUID id;
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;
}
