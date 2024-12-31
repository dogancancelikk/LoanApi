package com.example.creditmodule.service;

import com.example.creditmodule.entity.CustomerEntity;
import com.example.creditmodule.entity.LoanEntity;
import com.example.creditmodule.entity.LoanInstallmentEntity;
import com.example.creditmodule.exception.ApiException;
import com.example.creditmodule.mapper.LoanInstallmentMapper;
import com.example.creditmodule.mapper.LoanMapper;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.repository.LoanInstallmentRepository;
import com.example.creditmodule.repository.LoanRepository;
import com.example.creditmodule.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceUnitTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanMapper loanMapper;
    @Mock
    private LoanInstallmentMapper loanInstallmentMapper;

    @InjectMocks
    private LoanService loanService;

    private LoanEntity loan;
    private UUID loanId;
    private UUID customerId;
    private CustomerEntity customer;


    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = new CustomerEntity();
        customer.setId(customerId);
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(2000));

        loan = new LoanEntity();
        loan.setId(loanId);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setInterestRate(BigDecimal.valueOf(0.2));
        loan.setNumberOfInstallment(6);
        loan.setCreateDate(LocalDate.now());
        loan.setCustomerId(customerId);
        loan.setCustomer(customer);

        List<LoanInstallmentEntity> installments = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            installments.add(generateInstallment(LocalDate.now().plusMonths(i + 1).withDayOfMonth(1)));
        }
        loan.setInstallments(installments);
    }

    private LoanInstallmentEntity generateInstallment(LocalDate dueDate) {
        LoanInstallmentEntity installment = new LoanInstallmentEntity();
        installment.setId(UUID.randomUUID());
        installment.setLoanId(loanId);
        installment.setAmount(BigDecimal.valueOf(loan.getLoanAmount().doubleValue() * 1.2 / 6));
        installment.setDueDate(dueDate);
        installment.setPaid(false);
        return installment;
    }

    @Test
    void testPayLoan_Success() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        loan.setPaid(false);

        when(loanInstallmentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal paymentAmount = BigDecimal.valueOf(2000);
        PaymentResponse paymentResponse = loanService.payLoan(loanId, paymentAmount);

        assertNotNull(paymentResponse);
        assertTrue(paymentResponse.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        assertFalse(paymentResponse.getIsLoanFullyPaid());

        assertTrue(paymentResponse.getCountOfPaidInstallments() > 0);

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentRepository, times(1)).saveAll(anyList());
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void testPenaltyLoan_Success() {
        LoanInstallmentEntity oldInstallment =
                generateInstallment(LocalDate.now().minusMonths(1).withDayOfMonth(1));
        loan.getInstallments().add(oldInstallment);
        loan.setPaid(false);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal paymentAmount = BigDecimal.valueOf(3000);
        PaymentResponse paymentResponse = loanService.payLoan(loanId, paymentAmount);

        assertNotNull(paymentResponse);
        assertTrue(paymentResponse.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        assertFalse(paymentResponse.getIsLoanFullyPaid());
        assertTrue(paymentResponse.getCountOfPaidInstallments() > 0);

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentRepository, times(1)).saveAll(anyList());
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void testPayLoanThrowExceptionWhenLoanNotFound() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> loanService.payLoan(loanId, BigDecimal.valueOf(500)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Loan not found.", ex.getMessage());

        verify(loanInstallmentRepository, never()).saveAll(anyList());
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void testPayLoanThrowExceptionAlreadyPaid() {
        loan.setPaid(true);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        ApiException ex = assertThrows(ApiException.class,
                () -> loanService.payLoan(loanId, BigDecimal.valueOf(500)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Loan is already fully paid.", ex.getMessage());

        verify(loanInstallmentRepository, never()).saveAll(anyList());
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void shouldPayOnlyThreeFutureInstallment() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        loan.setPaid(false);

        when(loanInstallmentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal paymentAmount = BigDecimal.valueOf(10000);
        PaymentResponse paymentResponse = loanService.payLoan(loanId, paymentAmount);

        assertNotNull(paymentResponse);
        assertFalse(paymentResponse.getIsLoanFullyPaid());
        assertTrue(paymentResponse.getTotalAmount().compareTo(paymentAmount) < 0);

        verify(loanRepository, times(1)).findById(loanId);

    }
    @Test
    void shouldPayAllSuccessfully() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        loan.setPaid(false);
        loan.setInstallments(List.of(generateInstallment(LocalDate.now().minusMonths(1).withDayOfMonth(1))));

        when(loanInstallmentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal paymentAmount = BigDecimal.valueOf(3000);
        PaymentResponse paymentResponse = loanService.payLoan(loanId, paymentAmount);
        assertTrue(paymentResponse.getIsLoanFullyPaid());
        verify(loanRepository, times(1)).findById(loanId);
        verify(loanRepository, times(1)).save(any());
    }
}
