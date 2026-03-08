package com.bootcamp.exception.product;

public class ProductAlreadyActivated extends RuntimeException {
    public ProductAlreadyActivated() {
        super("Product Already Activated");
    }
}
