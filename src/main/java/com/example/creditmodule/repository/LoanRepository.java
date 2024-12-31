package com.example.creditmodule.repository;

import com.example.creditmodule.entity.LoanEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID>, JpaSpecificationExecutor<LoanEntity> {
    List<LoanEntity> findByCustomerId(UUID customerId);

    default Specification<LoanEntity> getSpecification(
            UUID customerId, Integer numberOfInstallments, Boolean isPaid) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            if (numberOfInstallments != null) {
                predicates.add(criteriaBuilder.equal(root.get("numberOfInstallment"), numberOfInstallments));
            }
            if (isPaid != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPaid"), isPaid));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
