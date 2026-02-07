package com.blind.paylock.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
