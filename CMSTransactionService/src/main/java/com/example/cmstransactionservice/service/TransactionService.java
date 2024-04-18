package com.example.cmstransactionservice.service;

import com.example.cmstransactionservice.domain.Transaction;
import com.example.cmstransactionservice.dto.AllTransactionsResponseDto;
import com.example.cmstransactionservice.dto.SelectedCreditAccountTransactionsResponseDto;
import com.example.cmstransactionservice.dto.TransactionRequestDto;

public interface TransactionService {
    AllTransactionsResponseDto getTransactionsByCustomerId(String customerId, int page, int size,
                                                           String creditAccountId,
                                                           Character type,
                                                           String month,
                                                           String year);

    SelectedCreditAccountTransactionsResponseDto getTransactionsByCustomerIdAndCreditAccountId(String customerId,
                                                                                               String creditAccountId,
                                                                                               int page, int size,
                                                                                               Character type,
                                                                                               String month,
                                                                                               String year);

    Transaction addTransactionByCustomerId(String customerId, TransactionRequestDto transactionRequestDto);
}
