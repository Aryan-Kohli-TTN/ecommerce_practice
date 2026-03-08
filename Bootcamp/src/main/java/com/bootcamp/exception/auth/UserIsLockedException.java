package com.bootcamp.exception.auth;

public class UserIsLockedException extends RuntimeException {
    public UserIsLockedException(String message) {
        super(message);
    }
}
