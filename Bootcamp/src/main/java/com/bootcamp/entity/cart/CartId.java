package com.bootcamp.entity.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CartId {
    @Column(name="customer_id")
    private UUID customerId;

    @Column(name="product_variation_id")
    private UUID productVariationId;

    @Override
    public String toString() {
        return "CartId{" +
                "customerId=" + customerId +
                ", productVariationId=" + productVariationId +
                '}';
    }
}
