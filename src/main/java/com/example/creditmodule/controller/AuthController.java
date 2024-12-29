package com.example.creditmodule.controller;

import com.example.creditmodule.request.TokenRequest;
import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.service.AuthService;
import com.example.creditmodule.util.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiEndpoints.TOKEN)
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.generateToken(request.getUsername(), request.getPassword()));
    }

}
