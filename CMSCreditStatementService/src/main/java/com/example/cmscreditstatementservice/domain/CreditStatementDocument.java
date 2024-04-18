package com.example.cmscreditstatementservice.domain;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Document(collection = "credit_statements")
public class CreditStatementDocument {

    @Id
    private String creditStatementId;
    private String customerId;
    private Date creditStatementDate;
    private Date dueDate;
    private String creditStatementPeriod;
    private AccountDetails accountDetails;
    private List<TransactionDetails> payments;
    private List<TransactionDetails> credits;
    private CalculationDetails calculations;

    public static class AccountDetails {

        private String accountNumber;
        private BigDecimal creditLimit;

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
    }

    public static class TransactionDetails {

        private String transactionId;
        private Date transactionDate;
        private Date postDate;
        private String description;
        private BigDecimal amount;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public Date getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(Date transactionDate) {
            this.transactionDate = transactionDate;
        }

        public Date getPostDate() {
            return postDate;
        }

        public void setPostDate(Date postDate) {
            this.postDate = postDate;
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
    }

    public static class CalculationDetails {

        private BigDecimal previousBalance;
        private BigDecimal totalPayments;
        private BigDecimal totalCredits;
        private BigDecimal amountDue;
        private BigDecimal minimumPayment;
        private BigDecimal creditAvailable;

        public BigDecimal getPreviousBalance() {
            return previousBalance;
        }

        public void setPreviousBalance(BigDecimal previousBalance) {
            this.previousBalance = previousBalance;
        }

        public BigDecimal getTotalPayments() {
            return totalPayments;
        }

        public void setTotalPayments(BigDecimal totalPayments) {
            this.totalPayments = totalPayments;
        }

        public BigDecimal getTotalCredits() {
            return totalCredits;
        }

        public void setTotalCredits(BigDecimal totalCredits) {
            this.totalCredits = totalCredits;
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

    public String getCreditStatementId() {
        return creditStatementId;
    }

    public void setCreditStatementId(String creditStatementId) {
        this.creditStatementId = creditStatementId;
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

    public String getCreditStatementPeriod() {
        return creditStatementPeriod;
    }

    public void setCreditStatementPeriod(String creditStatementPeriod) {
        this.creditStatementPeriod = creditStatementPeriod;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public List<TransactionDetails> getPayments() {
        return payments;
    }

    public void setPayments(List<TransactionDetails> payments) {
        this.payments = payments;
    }

    public List<TransactionDetails> getCredits() {
        return credits;
    }

    public void setCredits(List<TransactionDetails> credits) {
        this.credits = credits;
    }

    public CalculationDetails getCalculations() {
        return calculations;
    }

    public void setCalculations(CalculationDetails calculations) {
        this.calculations = calculations;
    }
}


/*


====================================================================================================================================================
                                                               Credit Management System E-statement

  ASHWIN RAJPUT
  xx xxxxxxxx xx, xxxxxxxx, xx, xxx xxx

- Statement Id     : 186920230401in66
- Account Number   : 6943578223751869
- Statement Date   : 2023-04-01
- Statement Period : 2023-03-01 to 2023-03-31

  Account Summary ------------------------------------
- Credit Limit     : $1,500.00
- Credit Available : $1,500.00
- Amount Due       : $.00
- Minimum Payment  : $.00
- Due Date         : 2023-04-30

  Transactions ---------------------------------------

- [Your Payments]
----------------------------------------------------------------------------------------------------------------------------------------------
  Transaction ID    Transaction Date    Post Date     Description                                                                       Amount
----------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------
  * Total Payments                                                                                                                        $.00

- [Your Credits and Charges]
----------------------------------------------------------------------------------------------------------------------------------------------
  Transaction ID    Transaction Date    Post Date     Description                                                                       Amount
----------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------
  * Total Credits                                                                                                                         $.00


- Amount Due = Previous Balance + (Total Credits - Total Payments)
             = $.00 + ( $.00 - $.00 )
             = $.00

====================================================================================================================================================

*/