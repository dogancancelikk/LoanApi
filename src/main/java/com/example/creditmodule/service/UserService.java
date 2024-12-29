package com.example.creditmodule.service;


import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.entity.lookup.UserRole;
import com.example.creditmodule.repository.UserRepository;
import com.example.creditmodule.request.CreateAdminRequest;
import lombok.RequiredArgsConstructor;
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
            throw new RuntimeException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ROLE_CUSTOMER);
        return userRepository.save(user);
    }
}
