package com.bootcamp.exception.paging;

public class InvalidPageOffsetException extends RuntimeException {
    public InvalidPageOffsetException() {
        super("Invalid Page Offset must be Integer and greater than equal to zero");
    }
}
