package com.example.creditmodule.repository;

import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByUserUsername(String username);
}
