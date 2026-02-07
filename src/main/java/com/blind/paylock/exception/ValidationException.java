package com.blind.paylock.exception;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}
