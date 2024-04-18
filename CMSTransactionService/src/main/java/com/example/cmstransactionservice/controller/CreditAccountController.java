package com.example.cmstransactionservice.controller;

import com.example.cmstransactionservice.controller.exception.UnexpectedException;
import com.example.cmstransactionservice.dto.AllCreditAccountsResponseDto;
import com.example.cmstransactionservice.service.CreditAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    public CreditAccountController(CreditAccountService creditAccountService) {
        this.creditAccountService = creditAccountService;
    }

    // - GET - /api/v1/customer/{customer-id}/transactions/credit-accounts ( view all accounts )
    @GetMapping(value = "/{customer-id}/transactions/credit-accounts", produces = {"application/json"})
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public AllCreditAccountsResponseDto getAllCreditAccountsForCustomer(@PathVariable("customer-id") String customerId) {

        AllCreditAccountsResponseDto allCreditAccountsResponseDto;
        try {
            allCreditAccountsResponseDto = creditAccountService
                    .getCreditAccountsByCustomerId(customerId);
        } catch (Exception e) {
            throw new UnexpectedException("Unable to fetch Credit Accounts. Please try again later.");
        }

        return allCreditAccountsResponseDto;
    }
}


