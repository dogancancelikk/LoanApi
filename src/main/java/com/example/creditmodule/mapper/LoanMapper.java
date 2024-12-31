package com.example.creditmodule.mapper;

import com.example.creditmodule.entity.LoanEntity;
import com.example.creditmodule.response.LoanResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",
        uses = {LoanInstallmentMapper.class})
public interface LoanMapper {
    LoanResponse toResponse(LoanEntity request);
    default Page<LoanResponse> mapAll(Page<LoanEntity> loans) {
        return loans.map(this::toResponse);
    }
}
