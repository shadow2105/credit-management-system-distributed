package com.example.cmstransactionservice.service;

import com.example.cmstransactionservice.domain.repository.CreditAccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionAccessValidator {

    private final CreditAccountRepository creditAccountRepository;

    public TransactionAccessValidator(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    public boolean hasAccessToCreditAccount(String creditAccountId) {
        String customerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return creditAccountRepository.existsByIdAndCustomerId(UUID.fromString(creditAccountId), customerId);
    }
}
