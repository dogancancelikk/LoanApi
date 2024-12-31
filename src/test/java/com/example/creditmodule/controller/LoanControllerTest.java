package com.example.creditmodule.controller;

import com.example.creditmodule.TestBase;
import com.example.creditmodule.TestData;
import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.LoanEntity;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.repository.LoanRepository;
import com.example.creditmodule.request.CreateLoanRequest;
import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.util.ApiEndpoints;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoanControllerTest extends TestBase {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;


    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    void shouldCreateLoanSuccessfully() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        CreateLoanRequest loanRequest = CreateLoanRequest.builder()
                .amount(new BigDecimal(100))
                .interestRate(new BigDecimal("0.2"))
                .numberOfInstallments(12)
                .build();

        CustomerEntity customer = customerRepository.findById(UUID.fromString(CUSTOMER_ID)).get();

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .post(url)
                        .headers(headers)
                        .content(asJson(loanRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        List<LoanEntity> loans = loanRepository.findAll();
        LoanEntity loanEntity = loans.getFirst();
        assertEquals(12, loanEntity.getNumberOfInstallment());
        CustomerEntity updatedCustomer = customerRepository.findById(UUID.fromString(CUSTOMER_ID)).get();
        assertTrue(updatedCustomer.getUsedCreditLimit().compareTo(customer.getUsedCreditLimit()) > 0);
    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldListAllLoansSuccessfully() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3))) // Assuming 3 loans in MULTIPLE_LOAN
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(20)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andReturn();
    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldFilterLoansWithNumberOfInstallment() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .param("numberOfInstallments", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].numberOfInstallment", is(6)))
                .andReturn();
    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldListLoansFilteredByIsPaid() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .param("isPaid", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].paid", is(false)))
                .andExpect(jsonPath("$.content[1].paid", is(false)))
                .andReturn();
    }

    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldListLoansFilteredByNumberOfInstallmentsAndIsPaid() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .param("numberOfInstallments", "9")
                        .param("isPaid", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].numberOfInstallment", is(9)))
                .andExpect(jsonPath("$.content[0].paid", is(false)))
                .andReturn();
    }
    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldListLoansSortedByLoanAmountDesc() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .param("sort", "loanAmount")
                        .param("order", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].loanAmount", is(25.0)))
                .andExpect(jsonPath("$.content[1].loanAmount", is(20.0)))
                .andExpect(jsonPath("$.content[2].loanAmount", is(20.0)))
                .andReturn();
    }
    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_LOAN, type = DatabaseOperation.CLEAN_INSERT)
    void shouldListLoansWithPagination() throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2))) // First page with 2 loans
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.last", is(false)))
                .andReturn();
    }
    @Test
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.MULTIPLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    void shouldReturnForbiddenWithAnotherCustomer() throws Exception {
        TokenResponse tokenForCustomer = getTokenForSecondCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(url)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @ParameterizedTest
    @CsvSource({
            "25.0,0.2,7",
            "0,0.2,12",
            "0.1,0.7,24",
    })
    @DatabaseSetup(value = TestData.DEFAULT, type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseSetup(value = TestData.SINGLE_CUSTOMER, type = DatabaseOperation.CLEAN_INSERT)
    void shouldThrowBadRequest(
            BigDecimal amount,
            BigDecimal interestRate,
            Integer numberOfInstallments
    ) throws Exception {
        TokenResponse tokenForCustomer = getTokenForCustomer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + tokenForCustomer.getAccessToken());

        CreateLoanRequest loanRequest = CreateLoanRequest.builder()
                .amount(amount)
                .interestRate(interestRate)
                .numberOfInstallments(numberOfInstallments)
                .build();

        String url = ApiEndpoints.CUSTOMER + "/" + CUSTOMER_ID + ApiEndpoints.LOAN;
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post(url)
                        .headers(headers)
                        .content(asJson(loanRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


}