package com.example.creditmodule.controller;

import com.example.creditmodule.IntegrationTestBase;
import com.example.creditmodule.request.TokenRequest;
import com.example.creditmodule.util.ApiEndpoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTestBase {

    @Test
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