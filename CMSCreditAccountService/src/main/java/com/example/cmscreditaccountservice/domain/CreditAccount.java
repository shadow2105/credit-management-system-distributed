package com.example.cmscreditaccountservice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_accounts")
public class CreditAccount extends BaseEntityAudit {

    /*
     * In a typical application flow, since only logged-in users can create a credit account, the foreign key constraint
     * on customerId (references username) is naturally enforced at the application level. When a user is authenticated
     * and requests to creates a credit account, it would ensure that the associated customerId (username) corresponds
     * to a valid user in the authorization server.
     *
     * While there may not be a strict foreign key constraint at the database level in this case, the application logic
     * ensures that only valid and authorized users can create associated records in the 'credit_accounts' table.
     * It's a common practice when dealing with distributed microservices where maintaining strict database-level
     * foreign key constraints between services might be challenging.
     */
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "creditAccount", orphanRemoval = true)
    // primary key of the CreditAccount entity is used as the foreign key value for the associated CreditCard entity
    @PrimaryKeyJoinColumn
    private CreditCard creditCard;

    public CreditAccount() {
    }

    public CreditAccount(String customerId, String accountNumber, BigDecimal creditLimit, BigDecimal currentBalance,
                         BigDecimal interestRate) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.creditLimit = creditLimit;
        this.currentBalance = currentBalance;
        this.interestRate = interestRate;
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

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void addCreditCard(CreditCard creditCard) {
       creditCard.setCreditAccount(this);
       this.setCreditCard(creditCard);
    }

    @Override
    public String toString() {
        //DecimalFormat myFormatter = new DecimalFormat("###,###,###.00");
        return "CreditAccount{" +
                "customerId='" + customerId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", creditLimit=" + creditLimit +
                ", currentBalance=" + currentBalance +     // Should currentBalance be exposed?
                ", interestRate=" + interestRate +
                '}';
    }
}
