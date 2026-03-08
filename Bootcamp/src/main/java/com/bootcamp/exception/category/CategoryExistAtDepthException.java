package com.bootcamp.exception.category;

public class CategoryExistAtDepthException extends RuntimeException {
    public CategoryExistAtDepthException() {
        super("Category name already exist in chain");
    }
}
