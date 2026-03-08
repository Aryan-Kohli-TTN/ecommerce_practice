package com.bootcamp.exception.product;

public class ProductAlreadyExistException extends RuntimeException {
    public ProductAlreadyExistException() {
        super("Product Already exist");
    }
}
