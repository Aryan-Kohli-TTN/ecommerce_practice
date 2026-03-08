package com.bootcamp.exception.product;

import lombok.Getter;

@Getter
public class FieldNotAssociatedException extends RuntimeException {
    String field;
    public FieldNotAssociatedException(String field) {
        super(field+" is not associated with category");
        this.field=field;
    }
}
