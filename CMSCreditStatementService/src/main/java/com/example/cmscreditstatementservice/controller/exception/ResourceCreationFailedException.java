package com.example.cmscreditstatementservice.controller.exception;

public class ResourceCreationFailedException extends RuntimeException {
    public ResourceCreationFailedException(String message) {
        super(message);
    }
}
