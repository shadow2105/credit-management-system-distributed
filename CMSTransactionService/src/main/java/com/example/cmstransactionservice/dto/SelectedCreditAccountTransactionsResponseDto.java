package com.example.cmstransactionservice.dto;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class SelectedCreditAccountTransactionsResponseDto extends
        RepresentationModel<SelectedCreditAccountTransactionsResponseDto> {

    private String customerId;
    private String accountId;

    //private int totalPages;     // Not available for Slice; see Page
    private int currentPage;

    //private int totalTransactions; // Not available for Slice; see Page
    private int numberOfTransactions;

    private boolean isLastPage;
    private List<TransactionDetails> transactions;

    public static class TransactionDetails {

        private String transactionId;
        private Timestamp transactionDateTime;
        private Timestamp postDateTime;
        private String accountNumber;
        private String description;
        private BigDecimal amount;
        private char type;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public Timestamp getTransactionDateTime() {
            return transactionDateTime;
        }

        public void setTransactionDateTime(Timestamp transactionDateTime) {
            this.transactionDateTime = transactionDateTime;
        }

        public Timestamp getPostDateTime() {
            return postDateTime;
        }

        public void setPostDateTime(Timestamp postDateTime) {
            this.postDateTime = postDateTime;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public char getType() {
            return type;
        }

        public void setType(char type) {
            this.type = type;
        }
    }

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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDetails> transactions) {
        this.transactions = transactions;
    }
}

/* GET - /api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}
( view all transactions for a credit account belonging to the customer )
{
    "customerId": "value",
    "accountId": "value",
    "currentPage": "value",
    "numberOfTransactions": "value",
    "lastPage": "value"
    "transactions": [
        {
            "transactionId": "value",
            "transactionDateTime": "value",
            "postDateTime": "value",
            "accountNumber": "value",
            "description": "value",
            "amount": "value",
            "type": "value",
        }

        // another transaction
    ],
    "_links": {
        "self": { "href": "/api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}" },
        "creditAccount": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" },
        "allTransactions": { "href": "/api/v1/customer/{customer-id}/transactions" },
    }
}
 */