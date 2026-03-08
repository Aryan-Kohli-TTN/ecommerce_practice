package com.bootcamp.exception.product;

public class ProductVariationAlreadyExistException extends RuntimeException {
    public ProductVariationAlreadyExistException() {
        super("Product Variation AlreadyExist");
    }
}
