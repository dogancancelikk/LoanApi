package com.example.creditmodule.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class CreateCustomerRequest implements Serializable {
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private String username;
    private String password;
}
