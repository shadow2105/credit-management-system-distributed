package com.example.cmscreditstatementservice.service;

import com.example.cmscreditstatementservice.domain.*;
import com.example.cmscreditstatementservice.domain.repository.*;
import com.example.cmscreditstatementservice.dto.AllCreditStatementsResponseDto;
import com.example.cmscreditstatementservice.dto.CreditStatementRequestDto;
import com.example.cmscreditstatementservice.dto.SelectedCreditAccountCreditStatementsResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditStatementServiceImpl implements CreditStatementService {

    private static final int DAYS_FOR_DUE_DATE = 22;

    // 10 percent = 0.1
    private static final double PERCENT_CREDIT_LIMIT_FOR_MIN_PAYMENT = 0.1;

    private final CreditAccountRepository creditAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CreditStatementRepository creditStatementRepository;
    private final CreditStatementDocumentRepository creditStatementDocumentRepository;
    private final CreditStatementScheduleRepository creditStatementScheduleRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");

    public CreditStatementServiceImpl(CreditAccountRepository creditAccountRepository, TransactionRepository transactionRepository, CreditStatementRepository
            creditStatementRepository, CreditStatementDocumentRepository creditStatementDocumentRepository, CreditStatementScheduleRepository creditStatementScheduleRepository) {
        this.creditAccountRepository = creditAccountRepository;
        this.transactionRepository = transactionRepository;
        this.creditStatementRepository = creditStatementRepository;
        this.creditStatementDocumentRepository = creditStatementDocumentRepository;
        this.creditStatementScheduleRepository = creditStatementScheduleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AllCreditStatementsResponseDto getCreditStatementsByCustomerId(String customerId, int page, int size,
                                                                          String creditAccountId,
                                                                          String month,
                                                                          String year) {
        AllCreditStatementsResponseDto allCreditStatementsResponseDto = new AllCreditStatementsResponseDto();

        Specification<CreditStatement> spec = Specification.where(CreditStatementRepository.Specs.byCustomerId(customerId));

        if (creditAccountId != null) {
            spec = spec.and(CreditStatementRepository.Specs.byCreditAccountId(creditAccountId));
        }

        if (month != null) {
            spec = spec.and(CreditStatementRepository.Specs.byMonth(month));
        }

        if (year != null) {
            spec = spec.and(CreditStatementRepository.Specs.byYear(year));
        }

        Pageable paging = PageRequest.of(page, size);
        /*Slice<CreditStatement> creditStatementsPage = creditStatementRepository
                .findAllByCustomerIdOrderByCreditStatementDate(customerId, paging);*/

        Slice<CreditStatement> creditStatementsPage = creditStatementRepository
                .findAll(CreditStatementRepository.Specs.orderByCreditStatementDate(spec), paging);

        // Fetch persisted Customer CreditStatement(s) from data source
        List<CreditStatement> retrievedCreditStatements = creditStatementsPage.getContent();

        // Map each CreditStatements to AllCreditStatementsResponseDto.CreditStatementDetails
        List<AllCreditStatementsResponseDto.CreditStatementDetails> creditStatements = retrievedCreditStatements
                .stream()
                .map(retrievedCreditStatement -> {
                    AllCreditStatementsResponseDto.CreditStatementDetails creditStatement = new
                            AllCreditStatementsResponseDto.CreditStatementDetails();

                    Date creditStatementDate = retrievedCreditStatement.getCreditStatementDate();

                    creditStatement.setCreditStatementId(retrievedCreditStatement.getId().toString());
                    creditStatement.setCreditStatementDate(creditStatementDate);
                    creditStatement.setDueDate(retrievedCreditStatement.getDueDate());
                    creditStatement.setCreditStatementPeriod(getCreditStatementPeriod(creditStatementDate));
                    creditStatement.setAccountNumber(retrievedCreditStatement.getCreditAccount().getAccountNumber());
                    creditStatement.setAmountDue(retrievedCreditStatement.getAmountDue());
                    creditStatement.setMinimumPayment(retrievedCreditStatement.getMinimumPayment());

                    return creditStatement;
                })
                .toList();

        // Construct AllCreditStatementsResponseDto object
        allCreditStatementsResponseDto.setCustomerId(customerId);
        allCreditStatementsResponseDto.setCurrentPage(creditStatementsPage.getNumber());
        allCreditStatementsResponseDto.setNumberOfCreditStatements(creditStatementsPage.getNumberOfElements());
        allCreditStatementsResponseDto.setIsLastPage(creditStatementsPage.isLast());
        allCreditStatementsResponseDto.setCreditStatements(creditStatements);

        return allCreditStatementsResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SelectedCreditAccountCreditStatementsResponseDto getCreditStatementsByCustomerIdAndCreditAccountId(String customerId,
                                                                                                              String creditAccountId,
                                                                                                              int page, int size,
                                                                                                              String month,
                                                                                                              String year) {
        SelectedCreditAccountCreditStatementsResponseDto selectedCreditAccountCreditStatementsResponseDto = new
                SelectedCreditAccountCreditStatementsResponseDto();

        Specification<CreditStatement> spec = Specification.where(CreditStatementRepository.Specs.byCustomerId(customerId))
                .and(CreditStatementRepository.Specs.byCreditAccountId(creditAccountId));

        if (month != null) {
            spec = spec.and(CreditStatementRepository.Specs.byMonth(month));
        }

        if (year != null) {
            spec = spec.and(CreditStatementRepository.Specs.byYear(year));
        }

        Pageable paging = PageRequest.of(page, size);
        /*Slice<CreditStatement> creditStatementsPage = creditStatementRepository
                .findAllByCustomerIdAndCreditAccount_IdOrderByCreditStatementDate(customerId,
            UUID.fromString(creditAccountId), paging);*/


        Slice<CreditStatement> creditStatementsPage = creditStatementRepository
                .findAll(CreditStatementRepository.Specs.orderByCreditStatementDate(spec), paging);

        // Fetch persisted Customer CreditStatement(s) for the selected Credit Account from data source
        List<CreditStatement> retrievedCreditStatements = creditStatementsPage.getContent();

        // Map each CreditStatement to SelectedCreditAccountCreditStatementsResponseDto.CreditStatementDetails
        List<SelectedCreditAccountCreditStatementsResponseDto.CreditStatementDetails> creditStatements = retrievedCreditStatements
                .stream()
                .map(retrievedCreditStatement -> {
                    SelectedCreditAccountCreditStatementsResponseDto.CreditStatementDetails creditStatement = new
                            SelectedCreditAccountCreditStatementsResponseDto.CreditStatementDetails();

                    Date creditStatementDate = retrievedCreditStatement.getCreditStatementDate();

                    creditStatement.setCreditStatementId(retrievedCreditStatement.getId().toString());
                    creditStatement.setCreditStatementDate(creditStatementDate);
                    creditStatement.setDueDate(retrievedCreditStatement.getDueDate());
                    creditStatement.setCreditStatementPeriod(getCreditStatementPeriod(creditStatementDate));
                    creditStatement.setAccountNumber(retrievedCreditStatement.getCreditAccount().getAccountNumber());
                    creditStatement.setAmountDue(retrievedCreditStatement.getAmountDue());
                    creditStatement.setMinimumPayment(retrievedCreditStatement.getMinimumPayment());

                    return creditStatement;
                })
                .toList();

        // Construct SelectedCreditAccountCreditStatementsResponseDto object
        selectedCreditAccountCreditStatementsResponseDto.setCustomerId(customerId);
        selectedCreditAccountCreditStatementsResponseDto.setAccountId(creditAccountId);
        selectedCreditAccountCreditStatementsResponseDto.setCurrentPage(creditStatementsPage.getNumber());
        selectedCreditAccountCreditStatementsResponseDto.setNumberOfCreditStatements(
                creditStatementsPage.getNumberOfElements());

        selectedCreditAccountCreditStatementsResponseDto.setIsLastPage(creditStatementsPage.isLast());
        selectedCreditAccountCreditStatementsResponseDto.setCreditStatements(creditStatements);

        return selectedCreditAccountCreditStatementsResponseDto;
    }

    @Override
    public CreditStatementDocument getRequestedCreditStatementByCustomerId(
            String customerId, CreditStatementRequestDto creditStatementRequestDto) {

        String creditAccountId = creditStatementRequestDto.getCreditAccountId();

        // Fetch persisted Customer CreditAccount(s) from data source
        Optional<CreditAccount> creditAccountOptional = creditAccountRepository
                .findByIdAndCustomerId(UUID.fromString(creditAccountId), customerId);

        if (creditAccountOptional.isEmpty()) {
            throw new RuntimeException("Failed to fetch CreditAccount. No CreditAccount found for the specified ID: "
                    + creditAccountId + " and customer ID: " + customerId);

        }

        CreditAccount selectedCreditAccount = creditAccountOptional.get();
        String accountNumber = selectedCreditAccount.getAccountNumber();
        int month = Integer.parseInt(creditStatementRequestDto.getMonth());
        int year = Integer.parseInt(creditStatementRequestDto.getYear());

        Optional<CreditStatementDocument> retrievedCreditStatementDocumentOptional = creditStatementDocumentRepository
                .findByCustomerIdAndAccountDetails_AccountNumberAndCreditStatementDateMonthAndCreditStatementDateYear(
                        customerId,
                        accountNumber,
                        month,
                        year
                );

        return retrievedCreditStatementDocumentOptional.orElse(null);
    }

    /*
    * cron = "seconds minutes hours day month day_of_week"
    * cron = "0 0 0 * * *" - At midnight (00:00:00) for every day of the month, every month and every day of the week
    * Run daily
    */
    @Override
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateCreditStatementsDaily() {
        // Save the current day
        LocalDate now = LocalDate.now();

        LocalDate creditStatementLocalDate = now.minusDays(1);
        LocalDate dueLocalDate = creditStatementLocalDate.plusDays(DAYS_FOR_DUE_DATE);

        // Start of Billing cycle
        LocalDateTime creditStatementPeriodStart = creditStatementLocalDate
                .minusMonths(1)
                .plusDays(1)
                .atStartOfDay();

        // End of Billing cycle
        LocalDateTime creditStatementPeriodEnd = creditStatementLocalDate
                .atTime(LocalTime.MAX);

        // Find & get all credit accounts which are due for a credit statement from credit_statement_schedule table
        // (that is those accounts whose statement day of the month was yesterday as this scheduled task runs at
        // midnight and calling LocalDate.now() would give the current day which is one more than the accounts' statement
        // day of the month)
        Set<CreditStatementSchedule> creditStatementSchedulesDue = creditStatementScheduleRepository
                .findAllByStatementDayOfMonth(creditStatementLocalDate.getDayOfMonth());

        if (creditStatementSchedulesDue.isEmpty()) {
            return;
        }

        // For each credit account schedule fetch account details and transactions within the credit statement period
        // (billing cycle) to create a basic CreditStatement object and the Credit Statement document
        creditStatementSchedulesDue
                .forEach(creditAccountSchedule -> {

                    CreditAccount creditAccount = creditAccountSchedule.getCreditAccount();
                    BigDecimal creditLimit = creditAccount.getCreditLimit();

                    // if the list of transactions is very large, filtering it in memory using streams is not the most
                    // efficient approach, as it could consume significant memory and processing resources.
                    /*Set<Transaction> transactionsInBillingCycle = creditAccount.getTransactions()
                            .stream()
                            .filter(transaction ->
                                    transaction.getTransactionDateTime()
                                            .compareTo(Timestamp.valueOf(creditStatementPeriodStart)) >= 0 &&
                                    transaction.getTransactionDateTime()
                                            .compareTo(Timestamp.valueOf(creditStatementPeriodEnd)) <= 0
                            )
                            .collect(Collectors.toSet());*/


                    // Since we are only interested in transactions within a specific date range, it's more efficient
                    // to leverage the database's query capabilities to retrieve only the relevant transactions directly
                    // from the database.
                    List<Transaction> creditAccountTransactionsInBillingCycle = transactionRepository
                            .findAllByCreditAccount_IdAndTransactionDateTimeBetweenOrderByTransactionDateTime(
                                    creditAccount.getId(),
                                    Timestamp.valueOf(creditStatementPeriodStart),
                                    Timestamp.valueOf(creditStatementPeriodEnd)
                            );


                    List<Transaction> creditAccountCreditTransactionsInBillingCycle = creditAccountTransactionsInBillingCycle
                            .stream()
                            .filter(transaction -> transaction.getType() == 'C')
                            .collect(Collectors.toList());

                    List<Transaction> creditAccountPaymentTransactionsInBillingCycle = creditAccountTransactionsInBillingCycle
                            .stream()
                            .filter(transaction -> transaction.getType() == 'P')
                            .collect(Collectors.toList());

                    Optional<CreditStatement> creditAccountPreviousCreditStatementOptional = creditStatementRepository.
                            findByCreditAccount_IdAndCreditStatementDate(
                                    creditAccount.getId(),
                                    Date.valueOf(creditStatementLocalDate.minusMonths(1)));

                    BigDecimal previousCreditStatementBalance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    if (creditAccountPreviousCreditStatementOptional.isPresent()) {
                        previousCreditStatementBalance = previousCreditStatementBalance
                                .add(creditAccountPreviousCreditStatementOptional.get()
                                .getAmountDue());
                    }


                    BigDecimal totalPayments = calculateTransactionsTotal(creditAccountPaymentTransactionsInBillingCycle);
                    BigDecimal totalCredits = calculateTransactionsTotal(creditAccountCreditTransactionsInBillingCycle);

                    // Amount Due = Previous Balance + (Total Credits - Total Payments)
                    BigDecimal amountDue = previousCreditStatementBalance.add(totalCredits.subtract(totalPayments));

                    BigDecimal minimumPayment = calculateMinimumPayment(amountDue, creditLimit);


                    // Construct and save the basic CreditStatement object
                    CreditStatement creditStatement = new CreditStatement(
                            Date.valueOf(creditStatementLocalDate),
                            Date.valueOf(dueLocalDate),
                            amountDue,
                            minimumPayment,
                            creditAccount);

                    creditStatement.setId(UUID.randomUUID());
                    creditStatement.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    creditStatement.setCreatedBy("cms-credit-statement-service");

                    CreditStatement savedCreditStatement = creditStatementRepository.save(creditStatement);


                    // Construct CreditStatementDocument object
                    CreditStatementDocument creditStatementDocument = new CreditStatementDocument();

                    // Construct CreditStatementDocument.AccountDetails
                    CreditStatementDocument.AccountDetails accountDetails = new
                            CreditStatementDocument.AccountDetails();

                    accountDetails.setAccountNumber(creditAccount.getAccountNumber());
                    accountDetails.setCreditLimit(creditLimit);

                    // Map each Transaction to CreditStatementDocument.TransactionDetails
                    List<CreditStatementDocument.TransactionDetails> payments = mapTransactionToTransactionDetails(
                            creditAccountPaymentTransactionsInBillingCycle);

                    List<CreditStatementDocument.TransactionDetails> credits = mapTransactionToTransactionDetails(
                            creditAccountCreditTransactionsInBillingCycle);

                    // Construct CreditStatementDocument.CalculationDetails
                    CreditStatementDocument.CalculationDetails calculations = new
                            CreditStatementDocument.CalculationDetails();

                    calculations.setPreviousBalance(previousCreditStatementBalance);
                    calculations.setTotalPayments(totalPayments);
                    calculations.setTotalCredits(totalCredits);
                    calculations.setAmountDue(amountDue);
                    calculations.setMinimumPayment(minimumPayment);

                    // Credit Available = Credit Limit - Amount Due
                    calculations.setCreditAvailable(creditLimit.subtract(amountDue));

                    Date creditStatementDate = savedCreditStatement.getCreditStatementDate();

                    creditStatementDocument.setCreditStatementId(savedCreditStatement.getId().toString());
                    creditStatementDocument.setCustomerId(creditAccount.getCustomerId());
                    creditStatement.setCreditStatementDate(creditStatementDate);
                    creditStatementDocument.setDueDate(savedCreditStatement.getDueDate());
                    creditStatementDocument.setCreditStatementPeriod(getCreditStatementPeriod(creditStatementDate));
                    creditStatementDocument.setAccountDetails(accountDetails);
                    creditStatementDocument.setPayments(payments);
                    creditStatementDocument.setCredits(credits);
                    creditStatementDocument.setCalculations(calculations);

                    creditStatementDocumentRepository.save(creditStatementDocument);

                    //sendEmailAboutCreditStatement(String email, String content);
                });


    }

    private String getCreditStatementPeriod(Date creditStatementDate) {
        // Credit statement date is the date on which the billing cycle ends, and it's the last day for transactions to
        // be included in that particular billing cycle's statement

        // Billing cycle (credit statement period) -
        //  - starts from one day after the credit statement date of the previous month
        //  - ends at the same day as the credit statement date of the current month
        // example; if credit statement date is 27 Jan 2024,
        //          then billing cycle is from 28 Dec 2023 to 27 Jan 2024,
        //          next billing cycle is from 28 Jan 2023 to 27 Feb 2024; credit statement date is 27 Feb 2024
        //          next billing cycle is from 28 Feb 2023 to 27 Mar 2024; credit statement date is 27 Mar 2024
        LocalDate creditStatementPeriodStart = creditStatementDate.toLocalDate()
                .minusMonths(1)
                .plusDays(1);

        LocalDate creditStatementPeriodEnd = creditStatementDate.toLocalDate();

        return creditStatementPeriodStart.format(formatter) + " to " + creditStatementPeriodEnd.format(formatter);
    }

    private BigDecimal calculateTransactionsTotal(List<Transaction> transactionList) {
        if (transactionList.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (Transaction transaction: transactionList) {
            total = total.add(transaction.getAmount());
        }

        return total;
    }

    private BigDecimal calculateMinimumPayment(BigDecimal amountDue, BigDecimal creditLimit) {
        if (amountDue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal percentOfCreditLimit = creditLimit.divide(BigDecimal.valueOf(PERCENT_CREDIT_LIMIT_FOR_MIN_PAYMENT),
                RoundingMode.HALF_UP);
        if (amountDue.compareTo(percentOfCreditLimit) < 0) {
            return amountDue;
        }
        else {
            return percentOfCreditLimit;
        }
    }

    private List<CreditStatementDocument.TransactionDetails> mapTransactionToTransactionDetails(
            List<Transaction> transactionList) {

        return transactionList
                .stream()
                .map(retrievedTransaction -> {
                    CreditStatementDocument.TransactionDetails transaction = new
                            CreditStatementDocument.TransactionDetails();

                    transaction.setTransactionId(retrievedTransaction.getId().toString());
                    transaction.setTransactionDate(new Date(
                            retrievedTransaction.getTransactionDateTime().getTime()));
                    transaction.setPostDate(new Date(
                            retrievedTransaction.getPostDateTime().getTime()));
                    transaction.setDescription(retrievedTransaction.getDescription());
                    transaction.setAmount(retrievedTransaction.getAmount());

                    return transaction;
                })
                .toList();
    }

    private void sendEmailAboutCreditStatement(String email, String content) {
        // Not implemented
    }
}