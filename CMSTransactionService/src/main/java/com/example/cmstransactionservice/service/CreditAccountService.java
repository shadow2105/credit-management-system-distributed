package com.example.cmstransactionservice.service;

import com.example.cmstransactionservice.dto.AllCreditAccountsResponseDto;

public interface CreditAccountService {

    AllCreditAccountsResponseDto getCreditAccountsByCustomerId(String customerId);
}
