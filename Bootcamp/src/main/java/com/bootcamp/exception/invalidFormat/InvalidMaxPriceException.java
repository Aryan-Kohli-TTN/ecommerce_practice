package com.bootcamp.exception.invalidFormat;

public class InvalidMaxPriceException extends RuntimeException {
    public InvalidMaxPriceException() {
        super("Invalid MaxPrice");
    }
}
