package com.example.creditmodule.controller;

import com.example.creditmodule.IntegrationTestBase;
import com.example.creditmodule.TestData;
import com.example.creditmodule.request.CreateCustomerRequest;
import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.service.CustomerService;
import com.example.creditmodule.util.ApiEndpoints;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest extends IntegrationTestBase {

    @Autowired
    public CustomerService customerService;

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
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
    void shouldNotAllowCustomerToCreateAnotherCustomer() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CreateCustomerRequest request = getGenerator().nextObject(CreateCustomerRequest.class);

        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());
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
    void getAllCustomers() {
    }
}