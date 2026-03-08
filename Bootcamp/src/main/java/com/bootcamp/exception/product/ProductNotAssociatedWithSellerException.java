package com.bootcamp.exception.product;

public class ProductNotAssociatedWithSellerException extends RuntimeException {
    public ProductNotAssociatedWithSellerException() {
        super("Product not associated with seller");
    }
}
