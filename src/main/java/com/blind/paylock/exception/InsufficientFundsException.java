package com.blind.paylock.exception;

public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException(String message) {
        super(message, "INSUFFICIENT_FUNDS");
    }
}
