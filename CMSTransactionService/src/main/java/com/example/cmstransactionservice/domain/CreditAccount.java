package com.example.cmstransactionservice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "credit_accounts")
public class CreditAccount extends BaseEntityAudit {

    /*
     * In a typical application flow, since only logged-in users can create a credit account, the foreign key constraint
     * on customerId (references username) is naturally enforced at the application level. When a user is authenticated
     * and requests to creates a credit account, it would ensure that the associated customerId (username) corresponds
     * to a valid user in the authorization server.
     *
     * This entity serves as a materialized view intended to store necessary columns from the 'credit_accounts' table
     * within the credit account service's database. It stores specific credit account details pertinent to the
     * transaction service. The data within this table remains synchronized with the credit account service, ensuring
     * updates occur whenever a new credit account is opened or an existing account is closed.
     */
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditAccount", orphanRemoval = true)
    private Set<Transaction> transactions;

    public CreditAccount() {
    }

    public CreditAccount(String customerId, String accountNumber, BigDecimal creditLimit, BigDecimal currentBalance,
                         Set<Transaction> transactions) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.creditLimit = creditLimit;
        this.currentBalance = currentBalance;
        this.transactions = transactions;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setCreditAccount(this);
        this.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return "CreditAccount{" +
                "id='" + this.getId() + '\'' +
                ", customerId='" + customerId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", creditLimit=" + creditLimit +
                ", currentBalance=" + currentBalance +
                '}';
    }
}
