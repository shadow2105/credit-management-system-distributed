package com.example.cmscreditstatementservice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "credit_statements")
public class CreditStatement extends BaseEntityAudit {

    @Column(name = "credit_statement_date", nullable = false)
    private Date creditStatementDate;

    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "amount_due", nullable = false)
    private BigDecimal amountDue;

    @Column(name = "minimum_payment", nullable = false)
    private BigDecimal minimumPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_account_id", referencedColumnName = "id")
    private CreditAccount creditAccount;

    public CreditStatement() {
    }

    public CreditStatement(Date creditStatementDate, Date dueDate, BigDecimal amountDue, BigDecimal minimumPayment,
                           CreditAccount creditAccount) {
        this.creditStatementDate = creditStatementDate;
        this.dueDate = dueDate;
        this.amountDue = amountDue;
        this.minimumPayment = minimumPayment;
        this.creditAccount = creditAccount;
    }

    public Date getCreditStatementDate() {
        return creditStatementDate;
    }

    public void setCreditStatementDate(Date creditStatementDate) {
        this.creditStatementDate = creditStatementDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public CreditAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(CreditAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    @Override
    public String toString() {
        return "CreditStatement{" +
                "id='" + this.getId() + '\'' +
                ", creditStatementDate=" + creditStatementDate +
                ", dueDate=" + dueDate +
                ", amountDue=" + amountDue +
                ", minimumPayment=" + minimumPayment +
                ", creditAccount=" + creditAccount +
                '}';
    }
}
