package com.bootcamp.exception.invalidFormat;

public class InvalidMinPriceException extends RuntimeException {
    public InvalidMinPriceException() {
        super("Invalid MinPrice");
    }
}
