package com.bootcamp.exception.category;

public class ParentCategoryHasProductException extends RuntimeException {
    public ParentCategoryHasProductException() {
        super("Parent Category has products");
    }
}
