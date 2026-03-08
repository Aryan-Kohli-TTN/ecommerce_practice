package com.bootcamp.exception.seller;

public class SellerMultipleAddressException extends RuntimeException {
    public SellerMultipleAddressException() {
        super("Seller can have only one address");
    }
}
