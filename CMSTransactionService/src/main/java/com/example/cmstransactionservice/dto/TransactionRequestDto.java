package com.example.cmstransactionservice.dto;

import com.example.cmstransactionservice.dto.validator.CharPattern;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class TransactionRequestDto {

    /*
     * @NotBlank: This annotation checks that a String is not null and the trimmed length is greater than zero.
     * In other words, it ensures that the String is not empty and contains at least one non-whitespace character.
     *
     * @NotNull: This annotation simply checks that a value is not null. It doesn't perform any checks on the content
     * of the value itself.
     */

    @NotBlank(message = "Credit Account ID is required")
    private String creditAccountId;

    @NotNull(message = "Transaction Type is required")
    @CharPattern(pattern = "[CP]", message = "Transaction type must be either 'C' or 'P'")
    private Character transactionType;

    @NotBlank(message = "Transaction Description is required")
    @Size(max = 200, message = "Transaction Description must be less than or equal to 200 characters")
    private String transactionDescription;

    @NotNull(message = "Transaction Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Transaction amount must be greater than 0")
    @Digits(integer=8, fraction=2, message = "Transaction amount is out of range")
    private BigDecimal amount;

    public String getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(String creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public Character getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Character transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
