package com.bootcamp.exception.paging;

public class InvalidPageSizeException extends RuntimeException {
    public InvalidPageSizeException() {
        super("Invalid Page Size must be Integer and greater than zero");
    }
}
