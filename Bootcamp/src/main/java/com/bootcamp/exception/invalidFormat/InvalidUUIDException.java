package com.bootcamp.exception.invalidFormat;

public class InvalidUUIDException extends RuntimeException{
    public InvalidUUIDException() {
        super("UUID Invalid format");
    }
}
