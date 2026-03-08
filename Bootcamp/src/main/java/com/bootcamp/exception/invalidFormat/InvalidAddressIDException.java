package com.bootcamp.exception.invalidFormat;

public class InvalidAddressIDException extends RuntimeException {
    public InvalidAddressIDException() {
        super("Invalid AddressId Exception");
    }
}
