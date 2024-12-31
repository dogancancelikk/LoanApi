package com.example.creditmodule.controller;

import com.example.creditmodule.TestBase;
import com.example.creditmodule.TestData;
import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.CustomerResponse;
import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.service.CustomerService;
import com.example.creditmodule.util.ApiEndpoints;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest extends TestBase {

    @Autowired
    public CustomerService customerService;

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.ADMIN_USER, type = DatabaseOperation.REFRESH)
    void shouldAdminCreateCustomerSuccessfully() throws Exception {
        TokenResponse tokenForAdmin = getTokenForAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CreateCustomerRequest request = getGenerator().nextObject(CreateCustomerRequest.class);

        headers.add("Authorization", "Bearer " + tokenForAdmin.getAccessToken());
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(ApiEndpoints.CUSTOMER)
                        .headers(headers)
                        .content(asJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.REFRESH)
    void shouldNotAllowCustomer() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        CreateCustomerRequest request = getGenerator().nextObject(CreateCustomerRequest.class);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(ApiEndpoints.CUSTOMER)
                        .headers(headers)
                        .content(asJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.ADMIN_USER, type = DatabaseOperation.REFRESH)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.REFRESH)
    void shouldAdminGetAllCustomers() throws Exception {
        TokenResponse tokenForAdmin = getTokenForAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForAdmin.getAccessToken());
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .get(ApiEndpoints.CUSTOMER)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        CustomerResponse customerResponse = objectMapper.convertValue(objectMapper.readValue(contentAsString, List.class).getFirst(), CustomerResponse.class);
        assertEquals(UUID.fromString(CUSTOMER_ID), customerResponse.getId());
    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.REFRESH)
    void shouldThrowWhenCustomerGetAllCustomers() throws Exception {
        TokenResponse tokenForAdmin = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForAdmin.getAccessToken());
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(ApiEndpoints.CUSTOMER)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

    }
}