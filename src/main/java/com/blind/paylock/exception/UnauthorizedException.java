package com.blind.paylock.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super("UNAUTHORIZED", "Access denied", HttpStatus.FORBIDDEN);
    }
}
