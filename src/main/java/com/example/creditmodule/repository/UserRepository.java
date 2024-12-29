package com.example.creditmodule.repository;

import com.example.creditmodule.entity.UserEntity;
import com.example.creditmodule.entity.lookup.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByRole(UserRole role);
}
