package com.bootcamp.exception.invalidFormat;

public class InvalidUserIDException extends RuntimeException {
    public InvalidUserIDException() {
        super("Invalid UserId Exception");
    }
}
