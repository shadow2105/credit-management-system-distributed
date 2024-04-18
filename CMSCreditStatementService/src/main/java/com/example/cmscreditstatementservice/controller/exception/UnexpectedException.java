package com.example.cmscreditstatementservice.controller.exception;

public class UnexpectedException extends RuntimeException{
    public UnexpectedException(String message) {
        super(message);
    }
}
