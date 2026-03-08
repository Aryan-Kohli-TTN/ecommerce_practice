package com.bootcamp.exception.product;

public class SecondaryImageMaxException extends RuntimeException {
    public SecondaryImageMaxException() {
        super("There can be max 10 secondary images");
    }
}
