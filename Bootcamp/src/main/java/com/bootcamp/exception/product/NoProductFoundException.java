package com.bootcamp.exception.product;

public class NoProductFoundException extends RuntimeException {
    public NoProductFoundException() {
        super("No Product Found");
    }
}
