package com.bootcamp.exception.product;

public class ProductNotActiveException extends RuntimeException {
    public ProductNotActiveException() {
        super("Product Not Active");
    }
}
