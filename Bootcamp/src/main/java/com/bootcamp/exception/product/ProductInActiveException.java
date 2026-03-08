package com.bootcamp.exception.product;

public class ProductInActiveException extends RuntimeException {
    public ProductInActiveException() {
        super("Product is inactive");
    }
}
