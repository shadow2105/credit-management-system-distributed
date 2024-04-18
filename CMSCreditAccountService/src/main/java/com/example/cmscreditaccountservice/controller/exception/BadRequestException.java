package com.example.cmscreditaccountservice.controller.exception;

class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
