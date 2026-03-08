package com.bootcamp.exception.user;

public class InvalidForgotPasswordTokenException extends RuntimeException {
    public InvalidForgotPasswordTokenException() {
        super("Invalid Forgot Password Token");
    }
}
