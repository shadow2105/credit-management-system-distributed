package com.example.cmscreditaccountservice.dto;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class SelectedCreditAccountResponseDto extends RepresentationModel<SelectedCreditAccountResponseDto> {

    private String customerId;
    private String accountId;
    private String accountNumber;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private BigDecimal creditAvailable;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getCreditAvailable() {
        return creditAvailable;
    }

    public void setCreditAvailable(BigDecimal creditAvailable) {
        this.creditAvailable = creditAvailable;
    }
}

/* GET - /api/v1/customer/{customer-id}/credit-accounts/{credit-account-id} ( view specific/selected account )
{
    "customerId": "value",
    "accountId": "value",
    "accountNumber": "value",
    "creditLimit": "value",
    "interestRate": "value",
    "currentBalance": "value",
    "creditAvailable": "value",
    "_links": {
        "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" },
        "transactions": { "href": "/api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}" },
        "creditStatements": { "href": "/api/v1/customer/{customer-id}/credit-statements/credit-accounts/{credit-account-id}" },
        "allCreditAccounts": { "href": "/api/v1/customer/{customer-id}/credit-accounts" },
     },
}
 */