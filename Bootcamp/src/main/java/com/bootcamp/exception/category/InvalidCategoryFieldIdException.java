package com.bootcamp.exception.category;

import lombok.Getter;

import java.util.UUID;
@Getter
public class InvalidCategoryFieldIdException extends RuntimeException {
    UUID categoryFieldID;
    public InvalidCategoryFieldIdException(UUID categoryFieldID) {
        super("Invalid Category Field Id");
        this.categoryFieldID=categoryFieldID;
    }
}
