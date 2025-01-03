package com.example.creditmodule;

import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.entity.lookup.UserRole;
import com.example.creditmodule.exception.ApiException;
import com.example.creditmodule.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitializeAdminUser {
    @Value("${application.admin.username}")
    private String username;

    @Value("${application.admin.password}")
    private String password;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {
        if (userRepository.existsByRole(UserRole.ROLE_ADMIN)) {
            return;
        }
        if (username == null || password == null) {
            throw new ApiException("Admin couldn't be initialized", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ROLE_ADMIN);
        userRepository.save(user);
    }
}