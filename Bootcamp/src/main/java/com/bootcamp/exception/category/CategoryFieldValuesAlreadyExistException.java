package com.bootcamp.exception.category;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryFieldValuesAlreadyExistException extends RuntimeException {
    UUID categoryId;
    UUID fieldId;
    public CategoryFieldValuesAlreadyExistException(UUID categoryId, UUID fieldId) {
        super("field values already exist");
        this.fieldId=fieldId;
        this.categoryId=categoryId;
    }
}
