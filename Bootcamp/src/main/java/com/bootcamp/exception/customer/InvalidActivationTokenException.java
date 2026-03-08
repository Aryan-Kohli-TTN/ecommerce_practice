package com.bootcamp.exception.customer;

public class InvalidActivationTokenException extends RuntimeException {
    public InvalidActivationTokenException() {
        super("Invalid Activation token");
    }
}
