package com.bootcamp.exception.invalidFormat;

public class NegativeNumberException extends RuntimeException {


    public NegativeNumberException(String number) {
        super(number);
    }
}
