package com.bootcamp.exception.category;

public class CategoryIsNonLeafException extends RuntimeException {
    public CategoryIsNonLeafException() {
        super("Category is non leaf");
    }
}
