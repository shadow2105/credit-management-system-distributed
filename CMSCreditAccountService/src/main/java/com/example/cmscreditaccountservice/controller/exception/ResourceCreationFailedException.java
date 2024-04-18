package com.example.cmscreditaccountservice.controller.exception;

public class ResourceCreationFailedException extends RuntimeException {
    public ResourceCreationFailedException(String message) {
        super(message);
    }
}
