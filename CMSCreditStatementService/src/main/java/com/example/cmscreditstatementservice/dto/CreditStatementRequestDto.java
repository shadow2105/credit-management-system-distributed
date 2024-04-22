package com.example.cmscreditstatementservice.dto;

public class CreditStatementRequestDto {

    private String creditAccountId;
    private String month;
    private String year;

    public CreditStatementRequestDto() {
    }

    public CreditStatementRequestDto(String creditAccountId, String month, String year) {
        this.creditAccountId = creditAccountId;
        this.month = month;
        this.year = year;
    }

    public String getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(String creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
