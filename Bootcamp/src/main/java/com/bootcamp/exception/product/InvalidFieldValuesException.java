package com.bootcamp.exception.product;

import lombok.Getter;

@Getter
public class InvalidFieldValuesException extends RuntimeException {
    String field;
    public InvalidFieldValuesException(String field) {
        super(field+" has invalid values");
        this.field=field;
    }
}
