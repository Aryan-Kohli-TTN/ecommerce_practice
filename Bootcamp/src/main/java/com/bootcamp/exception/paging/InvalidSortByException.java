package com.bootcamp.exception.paging;

public class InvalidSortByException extends RuntimeException {
    public InvalidSortByException() {
        super("Invalid SortBy");
    }
}
