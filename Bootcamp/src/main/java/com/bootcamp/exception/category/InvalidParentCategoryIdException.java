package com.bootcamp.exception.category;

public class InvalidParentCategoryIdException extends RuntimeException {
    public InvalidParentCategoryIdException() {
        super("Invalid Parent Category Id");
    }
}
