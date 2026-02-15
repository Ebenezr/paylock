package com.blind.paylock.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND);
    }
}
