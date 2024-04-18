package com.example.cmstransactionservice.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CharPatternValidator implements ConstraintValidator<CharPattern, Character> {

    private String pattern;

    @Override
    public void initialize(CharPattern constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull annotation handle null values
        }
        return value.toString().matches(pattern);
    }
}
