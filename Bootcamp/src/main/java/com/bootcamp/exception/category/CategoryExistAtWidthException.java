package com.bootcamp.exception.category;

public class CategoryExistAtWidthException extends RuntimeException {
    public CategoryExistAtWidthException() {
        super("Parent Category has sub categories with same name");
    }
}
