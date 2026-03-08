package com.bootcamp.exception.product;

public class VariationStructureNotMatchException extends RuntimeException {
    public VariationStructureNotMatchException() {
        super("Product Variation Structure does not match");
    }
}
