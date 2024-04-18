package com.example.cmscreditstatementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreditStatementRequestDto {

    // Regular expression for month (01-12)
    private static final String MONTH_PATTERN = "^(0?[1-9]|1[0-2])$";

    // Regular expression for year (starting from 1970 to 9999)
    // might cause unnecessary calls to the database
    //private static final String YEAR_PATTERN = "^(19[7-9]\\d|[2-9]\\d{3})$";

    // Regular expression for year (starting from 1970 to 2099)
    private static final String YEAR_PATTERN = "^(19[7-9]\\d|20\\d{2})$";

    /*
     * @NotBlank: This annotation checks that a String is not null and the trimmed length is greater than zero.
     * In other words, it ensures that the String is not empty and contains at least one non-whitespace character.
     *
     * @NotNull: This annotation simply checks that a value is not null. It doesn't perform any checks on the content
     * of the value itself.
     */

    @NotBlank(message = "Credit Account ID is required")
    private String creditAccountId;

    @NotNull(message = "Month of credit statement is required")
    @Pattern(regexp = MONTH_PATTERN, message = "Invalid month")
    private String month;

    @NotNull(message = "Year of credit statement is required")
    @Pattern(regexp = YEAR_PATTERN, message = "Invalid year")
    private String year;

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
