package com.bootcamp.exception.address;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super("Address Not Found");
    }
}
