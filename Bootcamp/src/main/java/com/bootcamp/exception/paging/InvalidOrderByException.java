package com.bootcamp.exception.paging;

public class InvalidOrderByException extends RuntimeException {
    public InvalidOrderByException() {
        super("Invalid Order By");
    }
}
