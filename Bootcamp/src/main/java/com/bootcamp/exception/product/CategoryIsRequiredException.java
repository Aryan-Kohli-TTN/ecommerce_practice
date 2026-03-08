package com.bootcamp.exception.product;

public class CategoryIsRequiredException extends RuntimeException {
    public CategoryIsRequiredException() {
        super("Category is required");
    }
}
