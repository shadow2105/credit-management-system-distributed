package com.example.cmscreditstatementservice.service;

import com.example.cmscreditstatementservice.dto.AllCreditAccountsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CreditAccountService {

    AllCreditAccountsResponseDto getCreditAccountsByCustomerId(String customerId);

    void processCreditAccountMessage(String message) throws JsonProcessingException;
}
