package com.bootcamp.exception.auth;

public class UserLoggedOutException extends RuntimeException {
    public UserLoggedOutException() {
        super("User logged out");
    }
}
