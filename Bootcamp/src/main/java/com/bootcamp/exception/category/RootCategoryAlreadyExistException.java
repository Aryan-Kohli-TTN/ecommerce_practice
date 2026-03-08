package com.bootcamp.exception.category;

public class RootCategoryAlreadyExistException extends RuntimeException {
    public RootCategoryAlreadyExistException() {
        super("Root Category Already Exist");
    }
}
