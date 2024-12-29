package com.example.creditmodule.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CreateAdminRequest implements Serializable {
    private String username;
    private String password;
}
