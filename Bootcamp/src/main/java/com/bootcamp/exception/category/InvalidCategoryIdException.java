package com.bootcamp.exception.category;

import lombok.Getter;

import java.util.UUID;
@Getter
public class InvalidCategoryIdException extends RuntimeException {
    UUID categoryId;
    public InvalidCategoryIdException(UUID categoryId) {
        super("Invalid Category Id");
        this.categoryId=categoryId;
    }
}
