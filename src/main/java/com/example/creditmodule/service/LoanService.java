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
import com.example.creditmodule.request.CreateLoanRequest;
import com.example.creditmodule.response.CreateLoanResponse;
import com.example.creditmodule.response.LoanInstallmentResponse;
import com.example.creditmodule.response.LoanResponse;
import com.example.creditmodule.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;
    private final LoanInstallmentMapper loanInstallmentMapper;


    @Transactional
    public CreateLoanResponse createLoan(UUID customerId, CreateLoanRequest request) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));

        validateRequest(request, customer);
        LoanEntity loan = saveLoan(request, customer);

        List<LoanInstallmentEntity> loanInstallments = saveInstallments(loan);
        loan.setInstallments(loanInstallments);
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(request.getAmount()));
        customerRepository.save(customer);
        return generateCreateLoanResponse(loan);
    }

    private CreateLoanResponse generateCreateLoanResponse(LoanEntity loan) {
        return CreateLoanResponse.builder()
                .loanId(loan.getId())
                .loanAmount(loan.getLoanAmount())
                .totalPayment(getTotalAmountWithInterestRate(loan))
                .installmentAmount(getInstallmentAmount(loan))
                .numberOfInstallments(loan.getNumberOfInstallment())
                .build();
    }

    private LoanEntity saveLoan(CreateLoanRequest request, CustomerEntity customer) {
        LoanEntity loan = new LoanEntity();
        loan.setId(UUID.randomUUID());
        loan.setCustomerId(customer.getId());
        loan.setLoanAmount(request.getAmount());
        loan.setCreateDate(LocalDate.now());
        loan.setInterestRate(request.getInterestRate());
        loan.setNumberOfInstallment(request.getNumberOfInstallments());
        loanRepository.save(loan);
        return loan;
    }

    private static void validateRequest(CreateLoanRequest request, CustomerEntity customer) {
        if (!List.of(6, 9, 12, 24).contains(request.getNumberOfInstallments())) {
            throw new ApiException("Number of installments must be one of 6, 9, 12, or 24.", HttpStatus.BAD_REQUEST);
        }

        if (!hasCreditLimit(request, customer)) {
            throw new ApiException("Credit Limit exceeded", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean hasCreditLimit(CreateLoanRequest request, CustomerEntity customer) {
        return customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(request.getAmount()) >= 0;
    }

    public Page<LoanResponse> listLoans(UUID customerId, Integer numberOfInstallments, Boolean isPaid, Pageable pageable) {
        Page<LoanEntity> loans = loanRepository.findAll(
                loanRepository.getSpecification(customerId, numberOfInstallments, isPaid),
                pageable);
        return loanMapper.mapAll(loans);
    }

    public List<LoanInstallmentEntity> listInstallments(UUID loanId) {
        LoanEntity loanEntity = loanRepository.findById(loanId).orElseThrow(() ->
                new ApiException("Loan Not Found", HttpStatus.NOT_FOUND));
        return loanEntity.getInstallments();
    }

    private List<LoanInstallmentEntity> saveInstallments(
            LoanEntity loan) {
        List<LoanInstallmentEntity> installments = new ArrayList<>();
        BigDecimal installmentAmount = getInstallmentAmount(loan);
        LocalDate firstDueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        for (int i = 0; i < loan.getNumberOfInstallment(); i++) {
            LoanInstallmentEntity installment = new LoanInstallmentEntity();
            installment.setId(UUID.randomUUID());
            installment.setAmount(installmentAmount);
            installment.setLoanId(loan.getId());
            installment.setDueDate(firstDueDate.plusMonths(i));
            installments.add(installment);
        }
        return loanInstallmentRepository.saveAll(installments);
    }

    private static BigDecimal getInstallmentAmount(LoanEntity loan) {
        return getTotalAmountWithInterestRate(loan)
                .divide(BigDecimal.valueOf(loan.getNumberOfInstallment()), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getTotalAmountWithInterestRate(LoanEntity loan) {
        return loan.getLoanAmount().multiply(loan.getInterestRate().add(BigDecimal.ONE));
    }

    @Transactional
    public PaymentResponse payLoan(UUID loanId, BigDecimal paymentAmount) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ApiException("Loan not found.", HttpStatus.NOT_FOUND));

        if (loan.isPaid()) {
            throw new ApiException("Loan is already fully paid.", HttpStatus.BAD_REQUEST);
        }
        List<LoanInstallmentEntity> unpaidInstallments = getUnpaidInstallments(loan);
        List<LoanInstallmentEntity> updatedInstallments = applyPaymentToInstallments(paymentAmount, unpaidInstallments);
        loanInstallmentRepository.saveAll(updatedInstallments);

        updateCustomerUsedLimit(loan, updatedInstallments.size());

        boolean isAllInstallmentsPaid = unpaidInstallments.size() == updatedInstallments.size();
        if (isAllInstallmentsPaid) {
            loan.setPaid(Boolean.TRUE);
            loanRepository.save(loan);
        }

        return PaymentResponse.builder()
                .countOfPaidInstallments(updatedInstallments.size())
                .totalAmount(getTotalPaymentOfLoan(updatedInstallments))
                .isLoanFullyPaid(isAllInstallmentsPaid)
                .remainingInstallments(getRemainingInstallments(unpaidInstallments, updatedInstallments))
                .build();
    }

    private List<LoanInstallmentResponse> getRemainingInstallments(List<LoanInstallmentEntity> unpaidInstallments, List<LoanInstallmentEntity> updatedInstallments) {
        List<UUID> updatedInstallmentIds = updatedInstallments.stream().map(LoanInstallmentEntity::getId).toList();
        List<LoanInstallmentEntity> remainingInstallments = unpaidInstallments.stream()
                .filter(loanInstallmentEntity -> !updatedInstallmentIds.contains(loanInstallmentEntity.getId()))
                .toList();

        return loanInstallmentMapper.toResponseList(remainingInstallments);
    }

    private BigDecimal getTotalPaymentOfLoan(List<LoanInstallmentEntity> updatedInstallments) {
        return updatedInstallments.stream()
                .map(LoanInstallmentEntity::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private List<LoanInstallmentEntity> applyPaymentToInstallments(BigDecimal paymentAmount, List<LoanInstallmentEntity> unpaidInstallments) {
        List<LoanInstallmentEntity> updatedInstallments = new ArrayList<>();

        for (LoanInstallmentEntity installment : getPayableInstallments(unpaidInstallments)) {
            BigDecimal calculatedAmountOfInstallment = calculatePaymentAmountOfInstallment(installment);
            paymentAmount = paymentAmount.subtract(calculatedAmountOfInstallment);
            if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
                break;
            }
            installment.setPaidAmount(calculatedAmountOfInstallment);
            installment.setPaid(Boolean.TRUE);
            installment.setPaymentDate(LocalDate.now());
            updatedInstallments.add(installment);
        }
        return updatedInstallments;
    }

    private List<LoanInstallmentEntity> getUnpaidInstallments(LoanEntity loan) {
        return loan.getInstallments()
                .stream()
                .filter(loanInstallmentEntity -> !loanInstallmentEntity.isPaid())
                .sorted(Comparator.comparing(LoanInstallmentEntity::getDueDate))
                .toList();

    }


    private void updateCustomerUsedLimit(LoanEntity loan, int countOfPaidInstallments) {
        CustomerEntity customer = loan.getCustomer();
        BigDecimal currentUsedCreditLimit = customer.getUsedCreditLimit();
        BigDecimal onePaymentOfInstallment = loan.getInstallments().getFirst().getAmount();
        customer.setUsedCreditLimit(currentUsedCreditLimit.add(onePaymentOfInstallment.multiply(new BigDecimal(countOfPaidInstallments))));
        customerRepository.save(customer);
    }

    private static List<LoanInstallmentEntity> getPayableInstallments(List<LoanInstallmentEntity> loanInstallmentEntities) {
        List<LoanInstallmentEntity> eligibleInstallments = loanInstallmentEntities.stream()
                .filter(installment -> installment
                        .getDueDate()
                        .isBefore(LocalDate.now().plusMonths(3)))
                .toList();

        if (eligibleInstallments.isEmpty()) {
            throw new ApiException("No unpaid installments available.", HttpStatus.BAD_REQUEST);
        }
        return eligibleInstallments;
    }

    private BigDecimal calculatePaymentAmountOfInstallment(LoanInstallmentEntity installment) {
        BigDecimal installmentAmount = installment.getAmount();
        LocalDate dueDate = installment.getDueDate();
        LocalDate paymentDate = LocalDate.now();

        if (paymentDate.equals(dueDate)) {
            return installmentAmount;
        }

        if (paymentDate.isBefore(dueDate)) {
            return applyDiscount(paymentDate, dueDate, installmentAmount);
        } else {
            return applyPenalty(dueDate, paymentDate, installmentAmount);
        }
    }

    private static BigDecimal applyPenalty(LocalDate dueDate, LocalDate paymentDate, BigDecimal installmentAmount) {
        BigDecimal penalty = calculateDueDateDifferences(dueDate, paymentDate, installmentAmount);
        return installmentAmount.add(penalty).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal applyDiscount(LocalDate paymentDate, LocalDate dueDate, BigDecimal installmentAmount) {
        BigDecimal discount = calculateDueDateDifferences(paymentDate, dueDate, installmentAmount);
        return installmentAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateDueDateDifferences(LocalDate dueDate, LocalDate paymentDate, BigDecimal installmentAmount) {
        long daysAfter = ChronoUnit.DAYS.between(dueDate, paymentDate);
        return installmentAmount.multiply(BigDecimal.valueOf(0.001))
                .multiply(BigDecimal.valueOf(daysAfter))
                .setScale(2, RoundingMode.HALF_UP);
    }
}