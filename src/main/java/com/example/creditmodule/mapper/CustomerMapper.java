package com.example.creditmodule.mapper;

import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.CustomerResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity toEntity(CreateCustomerRequest request);
    CustomerResponse toCustomerResponse(CustomerEntity entity);

    default List<CustomerResponse> mapAll(List<CustomerEntity> customers) {
        return customers.stream().map(this::toCustomerResponse).collect(Collectors.toList());
    }
}