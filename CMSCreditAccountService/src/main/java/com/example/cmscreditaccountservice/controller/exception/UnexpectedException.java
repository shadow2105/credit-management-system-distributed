package com.example.cmscreditaccountservice.controller.exception;

public class UnexpectedException extends RuntimeException{
    public UnexpectedException(String message) {
        super(message);
    }
}
