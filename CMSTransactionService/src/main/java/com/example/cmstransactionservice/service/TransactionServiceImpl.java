package com.example.cmstransactionservice.service;

import com.example.cmstransactionservice.domain.CreditAccount;
import com.example.cmstransactionservice.domain.Transaction;
import com.example.cmstransactionservice.domain.repository.CreditAccountRepository;
import com.example.cmstransactionservice.domain.repository.TransactionRepository;
import com.example.cmstransactionservice.dto.AllTransactionsResponseDto;
import com.example.cmstransactionservice.dto.SelectedCreditAccountTransactionsResponseDto;
import com.example.cmstransactionservice.dto.TransactionRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final int MIN_DAYS_TO_POST_TRANSACTION = 2;

    private static final int OFFSET_FROM_MIN_DAYS_TO_POST_TRANSACTION = 3;
    private final TransactionRepository transactionRepository;
    private final CreditAccountRepository creditAccountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, CreditAccountRepository creditAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AllTransactionsResponseDto getTransactionsByCustomerId(String customerId, int page, int size,
                                                                  String creditAccountId,
                                                                  Character type,
                                                                  String month,
                                                                  String year) {
        AllTransactionsResponseDto allTransactionsResponseDto = new AllTransactionsResponseDto();

        Specification<Transaction> spec = Specification.where(TransactionRepository.Specs.byCustomerId(customerId));

        if (creditAccountId != null) {
            spec = spec.and(TransactionRepository.Specs.byCreditAccountId(creditAccountId));
        }

        if (type != null) {
            spec = spec.and(TransactionRepository.Specs.byType(type));
        }

        if (month != null && year != null) {
            spec = spec.and(TransactionRepository.Specs.byMonthAndYear(month, year));
        }

        Pageable paging = PageRequest.of(page, size);
        /*Slice<Transaction> transactionsPage = transactionRepository
                .findAllByCustomerIdOrderByTransactionDateTime(customerId, paging);*/

        Slice<Transaction> transactionsPage = transactionRepository
                .findAll(TransactionRepository.Specs.orderByTransactionDateTime(spec), paging);


        // Fetch persisted Customer Transaction(s) from data source
        List<Transaction> retrievedTransactions = transactionsPage.getContent();

        // Map each Transaction to AllTransactionsResponseDto.TransactionDetails
        List<AllTransactionsResponseDto.TransactionDetails> transactions = retrievedTransactions
                .stream()
                .map(retrievedTransaction -> {
                    AllTransactionsResponseDto.TransactionDetails transaction = new
                            AllTransactionsResponseDto.TransactionDetails();

                    transaction.setTransactionId(retrievedTransaction.getId().toString());
                    transaction.setTransactionDateTime(retrievedTransaction.getTransactionDateTime());
                    transaction.setPostDateTime(retrievedTransaction.getPostDateTime());
                    transaction.setAccountNumber(retrievedTransaction.getCreditAccount().getAccountNumber());
                    transaction.setDescription(retrievedTransaction.getDescription());
                    transaction.setAmount(retrievedTransaction.getAmount());
                    transaction.setType(retrievedTransaction.getType());

                    return transaction;
                })
                .toList();

        // Construct AllTransactionsResponseDto object
        allTransactionsResponseDto.setCustomerId(customerId);
        allTransactionsResponseDto.setCurrentPage(transactionsPage.getNumber());
        allTransactionsResponseDto.setNumberOfTransactions(transactionsPage.getNumberOfElements());
        allTransactionsResponseDto.setIsLastPage(transactionsPage.isLast());
        allTransactionsResponseDto.setTransactions(transactions);

        return allTransactionsResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SelectedCreditAccountTransactionsResponseDto getTransactionsByCustomerIdAndCreditAccountId(String customerId,
                                                                                                      String creditAccountId,
                                                                                                      int page, int size,
                                                                                                      Character type,
                                                                                                      String month,
                                                                                                      String year)
    {
        SelectedCreditAccountTransactionsResponseDto selectedCreditAccountTransactionsResponseDto = new
                SelectedCreditAccountTransactionsResponseDto();

        Specification<Transaction> spec = Specification.where(TransactionRepository.Specs.byCustomerId(customerId))
                .and(TransactionRepository.Specs.byCreditAccountId(creditAccountId));

        if (type != null) {
            spec = spec.and(TransactionRepository.Specs.byType(type));
        }

        if (month != null && year != null) {
            spec = spec.and(TransactionRepository.Specs.byMonthAndYear(month, year));
        }

        Pageable paging = PageRequest.of(page, size);
        /*Slice<Transaction> transactionsPage = transactionRepository
                .findAllByCustomerIdAndCreditAccount_IdOrderByTransactionDateTime(customerId,
            UUID.fromString(creditAccountId), paging);*/

        Slice<Transaction> transactionsPage = transactionRepository
                .findAll(TransactionRepository.Specs.orderByTransactionDateTime(spec), paging);

        // Fetch persisted Customer Transaction(s) for the selected Credit Account from data source
        List<Transaction> retrievedTransactions = transactionsPage.getContent();

        // Map each Transaction to SelectedCreditAccountTransactionsResponseDto.TransactionDetails
        List<SelectedCreditAccountTransactionsResponseDto.TransactionDetails> transactions = retrievedTransactions
                .stream()
                .map(retrievedTransaction -> {
                    SelectedCreditAccountTransactionsResponseDto.TransactionDetails transaction = new
                            SelectedCreditAccountTransactionsResponseDto.TransactionDetails();

                    transaction.setTransactionId(retrievedTransaction.getId().toString());
                    transaction.setTransactionDateTime(retrievedTransaction.getTransactionDateTime());
                    transaction.setPostDateTime(retrievedTransaction.getPostDateTime());
                    transaction.setAccountNumber(retrievedTransaction.getCreditAccount().getAccountNumber());
                    transaction.setDescription(retrievedTransaction.getDescription());
                    transaction.setAmount(retrievedTransaction.getAmount());
                    transaction.setType(retrievedTransaction.getType());

                    return transaction;
                })
                .toList();

        // Construct SelectedCreditAccountTransactionsResponseDto object
        selectedCreditAccountTransactionsResponseDto.setCustomerId(customerId);
        selectedCreditAccountTransactionsResponseDto.setAccountId(creditAccountId);
        selectedCreditAccountTransactionsResponseDto.setCurrentPage(transactionsPage.getNumber());
        selectedCreditAccountTransactionsResponseDto.setNumberOfTransactions(transactionsPage.getNumberOfElements());
        selectedCreditAccountTransactionsResponseDto.setIsLastPage(transactionsPage.isLast());
        selectedCreditAccountTransactionsResponseDto.setTransactions(transactions);

        return selectedCreditAccountTransactionsResponseDto;
    }

    @Override
    @Transactional
    public Transaction addTransactionByCustomerId(String customerId,
                                                  TransactionRequestDto transactionRequestDto) {

        String creditAccountId = transactionRequestDto.getCreditAccountId();

        // Fetch persisted Customer CreditAccount(s) from data source
        Optional<CreditAccount> creditAccountOptional = creditAccountRepository
                .findByIdAndCustomerId(UUID.fromString(creditAccountId), customerId);

        if (creditAccountOptional.isEmpty()) {
            throw new RuntimeException("Failed to fetch CreditAccount. No CreditAccount found for the specified ID: "
                    + creditAccountId + " and customer ID: " + customerId);

        }

        CreditAccount selectedCreditAccount = creditAccountOptional.get();
        char transactionType = transactionRequestDto.getTransactionType();
        String transactionDescription = transactionRequestDto.getTransactionDescription();
        BigDecimal transactionAmount = transactionRequestDto.getAmount();

        BigDecimal currentAccountBalance = selectedCreditAccount.getCurrentBalance();
        BigDecimal creditLimit = selectedCreditAccount.getCreditLimit();

        BigDecimal closingBalance = BigDecimal.ZERO;
        switch (transactionType) {
            case 'C' -> {
                closingBalance = currentAccountBalance.add(transactionAmount);

                if (closingBalance.compareTo(creditLimit) > 0) {
                    return null;
                }
            }

            case 'P' -> {
                closingBalance = currentAccountBalance.subtract(transactionAmount);
            }
        }

        // Build Transaction
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setTransactionDateTime(new Timestamp(System.currentTimeMillis()));
        transaction.setPostDateTime(calculateTransactionPostDateTime());
        transaction.setDescription(transactionDescription);
        transaction.setType(transactionType);
        transaction.setAmount(transactionAmount.setScale(2, RoundingMode.HALF_UP));
        transaction.setClosingBalance(closingBalance.setScale(2, RoundingMode.HALF_UP));
        transaction.setCreditAccount(selectedCreditAccount);

        selectedCreditAccount.setCurrentBalance(closingBalance);

        /*
        * The below piece of code caused the following exception -
        * "A different object with the same identifier value was already associated with the session"
        *
        * see: https://stackoverflow.com/questions/16246675/hibernate-error-a-different-object-with-the-same-identifier-value-was-already-a
        *
        */
        //selectedCreditAccount.addTransaction(transaction);

        /*
        * In practice, if the CreditAccount entity is already managed by the persistence context (typically when
        * retrieved from the database or created within the same transaction), you don't need to explicitly call save()
        * on the repository to update it. Instead, the changes to the CreditAccount entity will be automatically
        * synchronized with the database upon the transaction commit.
        */
        //creditAccountRepository.save(selectedCreditAccount);

        return transactionRepository.save(transaction);
    }

    // LocalDateTime objects are immutable, ensuring thread safety and reducing the risk of unintended side effects
    //
    // LocalDateTime is designed to be thread-safe, making it suitable for use in multi-threaded environments without
    // requiring external synchronization.
    private Timestamp calculateTransactionPostDateTime() {
        int daysToPost = (int) (OFFSET_FROM_MIN_DAYS_TO_POST_TRANSACTION * Math.random()) + MIN_DAYS_TO_POST_TRANSACTION;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postDateTime = now.plusDays(daysToPost);

        return Timestamp.valueOf(postDateTime);
    }

    // Calendar objects are mutable. This mutability can lead to unexpected behavior if not handled carefully.
    //
    // Calendar instances are not inherently thread-safe. If multiple threads access and modify the same Calendar
    // instance concurrently, it can result in data inconsistency or unexpected behavior.
    /*private Timestamp calculateTransactionPostDateTime() {

        int daysToPost = ((int) (OFFSET_FROM_MIN_DAYS_TO_POST_TRANSACTION * Math.random())) +
                MIN_DAYS_TO_POST_TRANSACTION;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, daysToPost);

        return new Timestamp(calendar.getTimeInMillis());
    }*/
}
