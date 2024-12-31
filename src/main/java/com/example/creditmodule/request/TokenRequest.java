package com.example.creditmodule.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TokenRequest implements Serializable {
    @NotBlank
    @Schema(
            description = "The username of admin or customer user",
            example = "admin"
    )
    private String username;
    @NotBlank
    @Schema(
            description = "The password of the user requesting the token",
            example = "admin"
    )
    private String password;
}
