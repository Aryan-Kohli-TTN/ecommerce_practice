package com.bootcamp.exception.address;

public class DefaultAddressDeleteException extends RuntimeException {
    public DefaultAddressDeleteException() {
        super("Default Address cannot be deleted");
    }
}
