package com.bootcamp.exception.category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException() {
        super("Category Not Found");
    }
}
