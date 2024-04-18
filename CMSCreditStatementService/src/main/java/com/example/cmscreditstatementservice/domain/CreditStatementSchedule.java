package com.example.cmscreditstatementservice.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "credit_statement_schedule")
public class CreditStatementSchedule {

    @Id
    @Column(name = "credit_account_id")
    private UUID id;

    @Column(name = "statement_day_of_month", nullable = false)
    private int statementDayOfMonth;

    @OneToOne(fetch = FetchType.LAZY)
    // primary key values will be copied from the CreditAccount entity.
    @MapsId
    @JoinColumn(name = "credit_account_id")
    private CreditAccount creditAccount;

    public CreditStatementSchedule() {
    }

    public CreditStatementSchedule(UUID id, int statementDayOfMonth, CreditAccount creditAccount) {
        this.id = id;
        this.statementDayOfMonth = statementDayOfMonth;
        this.creditAccount = creditAccount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getStatementDayOfMonth() {
        return statementDayOfMonth;
    }

    public void setStatementDayOfMonth(int statementDayOfMonth) {
        this.statementDayOfMonth = statementDayOfMonth;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    @Override
    public String toString() {
        return "CreditStatementSchedule{" +
                "id=" + id +
                ", statementDayOfMonth=" + statementDayOfMonth +
                '}';
    }
}
