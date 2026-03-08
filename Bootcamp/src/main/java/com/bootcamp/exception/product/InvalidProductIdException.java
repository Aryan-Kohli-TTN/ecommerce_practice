package com.bootcamp.exception.product;

public class InvalidProductIdException extends RuntimeException {
    public InvalidProductIdException() {
        super("Invalid Product Id");
    }
}
