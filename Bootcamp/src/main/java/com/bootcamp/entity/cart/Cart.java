package com.bootcamp.entity.cart;

import com.bootcamp.entity.product.ProductVariation;
import com.bootcamp.entity.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cart {
    @EmbeddedId
    CartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productVariationId")
    private ProductVariation productVariation;

    @Column(name = "quantity",nullable = false)
    private BigInteger quantity;

    @Column(name = "is_wish_list_item")
    private boolean isWishlistItem;

    public Cart(Customer customer, ProductVariation productVariation, BigInteger quantity, boolean isWishlistItem) {
        this.customer = customer;
        this.productVariation = productVariation;
        this.quantity = quantity;
        this.isWishlistItem = isWishlistItem;
        this.id=new CartId(customer.getId(),productVariation.getId());
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", isWishlistItem=" + isWishlistItem +
                '}';
    }
}
