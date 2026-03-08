package com.bootcamp.exception.auth;

public class OldActivationTokenException extends RuntimeException {
    public OldActivationTokenException() {
        super("old activation token");
    }
}
