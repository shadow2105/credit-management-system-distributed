package com.example.cmscreditstatementservice.dto;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class AllCreditStatementsResponseDto extends RepresentationModel<AllCreditStatementsResponseDto> {

    private String customerId;

    //private int totalPages;     // Not available for Slice; see Page
    private int currentPage;

    //private int totalCreditStatements; // Not available for Slice; see Page
    private int numberOfCreditStatements;
    private boolean isLastPage;
    private List<CreditStatementDetails> creditStatements;


    public static class CreditStatementDetails {

        private String creditStatementId;
        private Date creditStatementDate;
        private Date dueDate;
        private String creditStatementPeriod;
        private String accountNumber;
        private BigDecimal amountDue;
        private BigDecimal minimumPayment;

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

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
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
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfCreditStatements() {
        return numberOfCreditStatements;
    }

    public void setNumberOfCreditStatements(int numberOfCreditStatements) {
        this.numberOfCreditStatements = numberOfCreditStatements;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<CreditStatementDetails> getCreditStatements() {
        return creditStatements;
    }

    public void setCreditStatements(List<CreditStatementDetails> creditStatements) {
        this.creditStatements = creditStatements;
    }
}

/* GET - /api/v1/customer/{customer-id}/credit-statements
( view all credit statements for the customer; or filter by ?creditAccountId= )
{
    "customerId": "value",
    "currentPage": "value",
    "numberOfCreditStatements": "value",
    "lastPage": "value"
    "creditStatements": [
        {
            "creditStatementId": "value",
            "creditStatementDate": "value",
            "dueDate": "value",
            "creditStatementPeriod": "value",
            "accountNumber": "value",
            "amountDue": "value",
            "minimumPayment": "value",
            "_links": {
                "download": { "href": "/api/v1/customer/not-implemented" }
            }
        }

        // another credit statement
    ],
    "_links": {
        "self": { "href": "/api/v1/customer/{customer-id}/credit-statements" }
    }
}
 */