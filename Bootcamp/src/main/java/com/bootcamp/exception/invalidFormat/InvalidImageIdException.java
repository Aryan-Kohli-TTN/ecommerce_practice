package com.bootcamp.exception.invalidFormat;

public class InvalidImageIdException extends RuntimeException {
    public InvalidImageIdException() {
        super("Invalid Image id");
    }
}
