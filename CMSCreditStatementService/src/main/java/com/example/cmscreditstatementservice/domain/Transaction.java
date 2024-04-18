package com.example.cmscreditstatementservice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private UUID id;

    @Column(name = "transaction_datetime", nullable = false)
    private Timestamp transactionDateTime;

    @Column(name = "post_datetime", nullable = false)
    private Timestamp postDateTime;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    private char type;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "closing_balance", nullable = false)
    private BigDecimal closingBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_account_id", referencedColumnName = "id")
    private CreditAccount creditAccount;

    public Transaction() {
    }

    public Transaction(UUID id, Timestamp transactionDateTime, Timestamp postDateTime, String description, char type,
                       BigDecimal amount, BigDecimal closingBalance, CreditAccount creditAccount) {
        this.id = id;
        this.transactionDateTime = transactionDateTime;
        this.postDateTime = postDateTime;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.closingBalance = closingBalance;
        this.creditAccount = creditAccount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", transactionDateTime=" + transactionDateTime +
                ", postDateTime=" + postDateTime +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", closingBalance=" + closingBalance +
                ", creditAccount=" + creditAccount +
                '}';
    }
}
