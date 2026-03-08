package com.bootcamp.exception.product;

public class ProductAlreadyDeactivated extends RuntimeException {
    public ProductAlreadyDeactivated() {
        super("Product Already Deactivated");
    }
}
