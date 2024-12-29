package com.example.creditmodule.service;


import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.security.JwtService;
import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TokenResponse generateToken(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Optional<CustomerEntity> customerEntity = customerRepository
                .findByUserUsername(username);
        if (customerEntity.isPresent()) {
            return jwtService.generateTokenForCustomer(customerEntity.get());
        }

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return jwtService.generateTokenForAdmin(userEntity);
    }

}
