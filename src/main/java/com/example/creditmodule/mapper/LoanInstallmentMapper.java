package com.example.creditmodule.mapper;

import com.example.creditmodule.entity.LoanInstallmentEntity;
import com.example.creditmodule.response.LoanInstallmentResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LoanInstallmentMapper {
    LoanInstallmentResponse toResponse(LoanInstallmentEntity request);

    default List<LoanInstallmentResponse> toResponseList(List<LoanInstallmentEntity> installmentEntities) {
        return installmentEntities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    ;
}
