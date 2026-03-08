package com.bootcamp.exception.seller;

public class DuplicateGstNoException extends RuntimeException {
    public DuplicateGstNoException() {
        super("Gst No Already Registered");
    }
}
