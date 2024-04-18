package com.example.cmscreditstatementservice.service;

import com.example.cmscreditstatementservice.domain.repository.CreditAccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreditStatementAccessValidator {

    private final CreditAccountRepository creditAccountRepository;

    public CreditStatementAccessValidator(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    public boolean hasAccessToCreditAccount(String creditAccountId) {
        String customerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return creditAccountRepository.existsByIdAndCustomerId(UUID.fromString(creditAccountId), customerId);
    }
}
