package com.example.creditmodule.repository;

import com.example.creditmodule.entity.LoanInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallmentEntity, UUID> {
}
