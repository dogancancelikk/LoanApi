package com.example.creditmodule;

import com.example.creditmodule.response.TokenResponse;
import com.example.creditmodule.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
@AutoConfigureMockMvc
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public abstract class TestBase {
    @Value("${application.admin.username}")
    protected String ADMIN_USERNAME;

    @Value("${application.admin.password}")
    protected String ADMIN_PASSWORD;

    protected String CUSTOMER_USERNAME = "test-customer-user";
    protected String CUSTOMER_PASSWORD = "test-password";
    protected String SECOND_CUSTOMER_USERNAME = "test-customer-user2";
    protected String CUSTOMER_ID = "cd4c88f4-c2e2-4b38-83dd-be00a65cea4a";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    public EasyRandom getGenerator() {
        return generator;
    }

    public TokenResponse getTokenForAdmin() throws Exception {
        return authService.generateToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public TokenResponse getTokenForCustomer() throws Exception {
        return authService.generateToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);
    }
    public TokenResponse getTokenForSecondCustomer() throws Exception {
        return authService.generateToken(SECOND_CUSTOMER_USERNAME, CUSTOMER_PASSWORD);
    }

    protected String asJson(Object object) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }
}
