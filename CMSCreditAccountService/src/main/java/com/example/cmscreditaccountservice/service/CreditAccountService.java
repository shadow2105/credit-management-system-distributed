package com.example.cmscreditaccountservice.service;

import com.example.cmscreditaccountservice.domain.CreditAccount;
import com.example.cmscreditaccountservice.dto.AllCreditAccountsResponseDto;
import com.example.cmscreditaccountservice.dto.CreditAccountOpeningRequestDto;
import com.example.cmscreditaccountservice.dto.SelectedCreditAccountResponseDto;

public interface CreditAccountService {
    AllCreditAccountsResponseDto getCreditAccountsByCustomerId(String customerId);

    SelectedCreditAccountResponseDto getCreditAccountByCustomerIdAndCreditAccountId(String customerId, String creditAccountId);

    CreditAccount addCreditAccountByCustomerId(String customerId,
                                               CreditAccountOpeningRequestDto creditAccountOpeningRequestDto);
}
