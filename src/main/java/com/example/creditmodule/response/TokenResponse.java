package com.example.creditmodule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TokenResponse implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    @JsonProperty("expires_in")
    private Long expiresIn;
}
