package com.bootcamp.exception.product;

public class ProductMetadataMinCountException extends RuntimeException {
    public ProductMetadataMinCountException() {
        super("Product variation must have one metaData field and value");
    }
}
