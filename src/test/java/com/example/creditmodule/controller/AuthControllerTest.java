package com.example.creditmodule.controller;

import com.example.creditmodule.TestBase;
import com.example.creditmodule.TestData;
import com.example.creditmodule.request.TokenRequest;
import com.example.creditmodule.util.ApiEndpoints;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends TestBase {

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.ADMIN_USER, type = DatabaseOperation.CLEAN_INSERT)
    void token() throws Exception {
        TokenRequest request = TokenRequest
                .builder()
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .build();
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(ApiEndpoints.AUTH + ApiEndpoints.TOKEN)
                        .content(asJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.ADMIN_USER, type = DatabaseOperation.CLEAN_INSERT)
    void shouldThrowExceptionWithIncorrectPassword() throws Exception {
        TokenRequest request = TokenRequest
                .builder()
                .username(ADMIN_USERNAME)
                .password("test-incorrect-password")
                .build();
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(ApiEndpoints.AUTH + ApiEndpoints.TOKEN)
                        .content(asJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}