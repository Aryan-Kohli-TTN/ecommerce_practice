package com.bootcamp.exception.product;

public class ProductVariationNotFoundException extends RuntimeException {
    public ProductVariationNotFoundException() {
        super("Product Variation Not found");
    }
}
