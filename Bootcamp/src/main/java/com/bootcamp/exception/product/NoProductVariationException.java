package com.bootcamp.exception.product;

public class NoProductVariationException extends RuntimeException {
    public NoProductVariationException() {
        super("Product has no variations");
    }
}
