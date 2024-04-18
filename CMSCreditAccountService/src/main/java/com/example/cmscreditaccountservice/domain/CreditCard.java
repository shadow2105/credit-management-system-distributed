package com.example.cmscreditaccountservice.domain;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "credit_cards")
public class CreditCard {

    @Id
    @Column(name = "credit_account_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    // primary key values will be copied from the CreditAccount entity.
    @MapsId
    @JoinColumn(name = "credit_account_id")
    private CreditAccount creditAccount;

    @Column(name = "cvv", nullable = false, unique = true)
    // encode it before saving
    private String cvv;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    public CreditCard() {
    }

    public CreditCard(UUID id, CreditAccount creditAccount, String cvv, Date expiryDate) {
        this.id = id;
        this.creditAccount = creditAccount;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreditCardNumber() {
        return creditAccount.getAccountNumber();
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", cardNumber=" + creditAccount.getAccountNumber() +
                ", cvv=[PROTECTED]" + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
