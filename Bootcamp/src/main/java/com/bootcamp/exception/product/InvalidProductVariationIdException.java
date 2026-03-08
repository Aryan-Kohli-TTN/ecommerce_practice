package com.bootcamp.exception.product;

public class InvalidProductVariationIdException extends RuntimeException {
    public InvalidProductVariationIdException() {
        super("Invalid Product Variation  Id");
    }
}
