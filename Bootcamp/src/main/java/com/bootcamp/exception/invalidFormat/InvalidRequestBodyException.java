package com.bootcamp.exception.invalidFormat;

public class InvalidRequestBodyException extends RuntimeException {
    public InvalidRequestBodyException() {
        super("Invalid Request Body");
    }
}
