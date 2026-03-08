package com.bootcamp.entity.product;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ProductReviewID implements Serializable {
    @Column(name="product_id")
    private UUID productId;

    @Column(name="customer_id")
    private UUID customerId;

    @Override
    public String toString() {
        return "ProductReviewID{" +
                "productId=" + productId +
                ", customerId=" + customerId +
                '}';
    }
}
