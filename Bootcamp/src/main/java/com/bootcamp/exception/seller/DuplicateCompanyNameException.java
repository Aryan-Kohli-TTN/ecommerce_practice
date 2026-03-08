package com.bootcamp.exception.seller;

public class DuplicateCompanyNameException extends RuntimeException {
    public DuplicateCompanyNameException() {
        super("Company Name Already Registered");
    }
}
