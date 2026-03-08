package com.bootcamp.exception.category;

public class CategoryFieldAlreadyExistException extends RuntimeException {
    public CategoryFieldAlreadyExistException() {
        super("Category Field Already Exist");
    }
}
