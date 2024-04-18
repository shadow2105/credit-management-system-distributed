package com.example.cmstransactionservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class AllCreditAccountsResponseDto {

    private String customerId;
    private List<CreditAccountDetails> creditAccounts;

    public static class CreditAccountDetails {

        private String accountId;
        private String accountNumber;
        private BigDecimal creditLimit;
        private BigDecimal currentBalance;
        private BigDecimal creditAvailable;

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CreditAccountDetails> getCreditAccounts() {
        return creditAccounts;
    }

    public void setCreditAccounts(List<CreditAccountDetails> creditAccounts) {
        this.creditAccounts = creditAccounts;
    }
}

/* GET - /api/v1/customer/{customer-id}/transactions/credit-accounts ( view all accounts )
{
    "customerId": "value",
    "creditAccounts": [
        {
            "accountId": "value",
            "accountNumber": "value",
            "creditLimit": "value",
            "currentBalance": "value",
            "creditAvailable": "value",
        }

        // another credit account
    ],
}
 */