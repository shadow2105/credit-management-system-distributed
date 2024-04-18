package com.example.cmscreditaccountservice.dto;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

public class AllCreditAccountsResponseDto extends RepresentationModel<AllCreditAccountsResponseDto> {

    private String customerId;
    private List<CreditAccountDetails> creditAccounts;
    private Totals totals;

    public static class CreditAccountDetails extends RepresentationModel<CreditAccountDetails> {

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

    public static class Totals {
        private BigDecimal totalCreditLimit;
        private BigDecimal totalCurrentBalance;
        private BigDecimal totalCreditAvailable;

        public BigDecimal getTotalCreditLimit() {
            return totalCreditLimit;
        }

        public void setTotalCreditLimit(BigDecimal totalCreditLimit) {
            this.totalCreditLimit = totalCreditLimit;
        }

        public BigDecimal getTotalCurrentBalance() {
            return totalCurrentBalance;
        }

        public void setTotalCurrentBalance(BigDecimal totalCurrentBalance) {
            this.totalCurrentBalance = totalCurrentBalance;
        }

        public BigDecimal getTotalCreditAvailable() {
            return totalCreditAvailable;
        }

        public void setTotalCreditAvailable(BigDecimal totalCreditAvailable) {
            this.totalCreditAvailable = totalCreditAvailable;
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

    public Totals getTotals() {
        return totals;
    }

    public void setTotals(Totals totals) {
        this.totals = totals;
    }
}

/* GET - /api/v1/customer/{customer-id}/credit-accounts ( view all accounts )
{
    "customerId": "value",
    "creditAccounts": [
        {
            "accountId": "value",
            "accountNumber": "value",
            "creditLimit": "value",
            "currentBalance": "value",
            "creditAvailable": "value",
            "_links": {
                "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" }
            }
        }

        // another credit account
    ],
    "totals": {
        "totalCreditLimit": "value",
        "totalCurrentBalance": "value",
        "totalCreditAvailable": "value"
    }
    "_links": {
        "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts" }
    }
}
 */