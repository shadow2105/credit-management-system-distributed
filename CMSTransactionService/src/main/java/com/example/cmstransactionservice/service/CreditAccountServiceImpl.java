package com.example.cmstransactionservice.service;

import com.example.cmstransactionservice.domain.CreditAccount;
import com.example.cmstransactionservice.domain.repository.CreditAccountRepository;
import com.example.cmstransactionservice.dto.AllCreditAccountsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditAccountServiceImpl implements CreditAccountService {

    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AllCreditAccountsResponseDto getCreditAccountsByCustomerId(String customerId) {
        AllCreditAccountsResponseDto allCreditAccountsResponseDto = new AllCreditAccountsResponseDto();

        // Fetch persisted Customer CreditAccount(s) from data source
        List<CreditAccount> retrievedCreditAccounts = creditAccountRepository
                .findAllByCustomerIdOrderByCreatedAt(customerId);

        // Map each CreditAccount to AllCreditAccountsResponseDto.CreditAccountDetails
        List<AllCreditAccountsResponseDto.CreditAccountDetails> creditAccounts = retrievedCreditAccounts
                .stream()
                .map(retrievedCreditAccount -> {
                    AllCreditAccountsResponseDto.CreditAccountDetails creditAccount = new
                            AllCreditAccountsResponseDto.CreditAccountDetails();

                    creditAccount.setAccountId(retrievedCreditAccount.getId().toString());
                    creditAccount.setAccountNumber((retrievedCreditAccount.getAccountNumber()));
                    creditAccount.setCreditLimit(retrievedCreditAccount.getCreditLimit());
                    creditAccount.setCurrentBalance(retrievedCreditAccount.getCurrentBalance());
                    creditAccount.setCreditAvailable((retrievedCreditAccount.getCreditLimit()
                            .subtract(retrievedCreditAccount.getCurrentBalance())));

                    return creditAccount;
                })
                .toList();

        // Construct AllCreditAccountsResponseDto object
        allCreditAccountsResponseDto.setCustomerId(customerId);
        allCreditAccountsResponseDto.setCreditAccounts(creditAccounts);

        return allCreditAccountsResponseDto;
    }
}
