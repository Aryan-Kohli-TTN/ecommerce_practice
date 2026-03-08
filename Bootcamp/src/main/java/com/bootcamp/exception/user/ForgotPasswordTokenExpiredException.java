package com.bootcamp.exception.user;

public class ForgotPasswordTokenExpiredException extends RuntimeException {
    public ForgotPasswordTokenExpiredException() {
        super("Forgot Password Token Expired");
    }
}
