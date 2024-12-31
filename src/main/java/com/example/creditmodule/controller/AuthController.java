package com.example.creditmodule.controller;

import com.example.creditmodule.request.TokenRequest;
import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.service.AuthService;
import com.example.creditmodule.util.ApiEndpoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.AUTH)
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiEndpoints.TOKEN)
    @Operation(
            summary = "Generate an access token",
            description = "Generates a JWT access token for the provided user credentials."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format or missing fields"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.generateToken(request.getUsername(), request.getPassword()));
    }

}
