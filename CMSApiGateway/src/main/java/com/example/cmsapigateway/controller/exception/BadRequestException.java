package com.example.cmsapigateway.controller.exception;

class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
