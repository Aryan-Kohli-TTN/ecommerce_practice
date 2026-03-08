package com.bootcamp.exception.user;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException() {
        super("Too many requests wait for 1 minute before retrying");
    }
}
