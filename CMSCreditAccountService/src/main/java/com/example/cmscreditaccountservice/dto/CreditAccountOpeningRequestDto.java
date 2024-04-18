package com.example.cmscreditaccountservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreditAccountOpeningRequestDto {

    @NotNull(message = "Gross Annual Household Income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Gross Annual Household Income must be greater than 0")
    @Digits(integer=8, fraction=2, message = "Gross Annual Household Income is out of range")
    private BigDecimal grossAnnualHouseholdIncome;

    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email must be less than or equal to 254 characters")
    private String email;

    public BigDecimal getGrossAnnualHouseholdIncome() {
        return grossAnnualHouseholdIncome;
    }

    public void setGrossHouseholdAnnualIncome(BigDecimal grossAnnualHouseholdIncome) {
        this.grossAnnualHouseholdIncome = grossAnnualHouseholdIncome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
