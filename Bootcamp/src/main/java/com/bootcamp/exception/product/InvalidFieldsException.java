package com.bootcamp.exception.product;

public class InvalidFieldsException extends RuntimeException {
    public InvalidFieldsException() {
        super("Invalid fields");
    }
}
