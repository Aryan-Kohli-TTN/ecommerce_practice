package com.bootcamp.exception.category;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryFieldValuesNotFoundException extends RuntimeException {
    UUID fieldId;
    public CategoryFieldValuesNotFoundException(UUID fieldId) {
        super("Metadata field values not found");
        this.fieldId=fieldId;
    }
}
