package com.example.creditmodule.service;


import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.entity.lookup.UserRole;
import com.example.creditmodule.exception.ApiException;
import com.example.creditmodule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity createCustomerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ApiException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ROLE_CUSTOMER);
        return userRepository.save(user);
    }
}
