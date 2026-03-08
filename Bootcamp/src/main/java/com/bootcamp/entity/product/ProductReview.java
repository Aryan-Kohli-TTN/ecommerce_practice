package com.bootcamp.entity.product;

import com.bootcamp.entity.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductReview {

    @EmbeddedId
    ProductReviewID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    private Customer customer;

    @Column(name = "review",nullable = false)
    private String review;

    @Column(name = "rating")
    private int rating;

    public ProductReview(Product product, Customer customer, String review, int rating) {
        this.product = product;
        this.customer = customer;
        this.review = review;
        this.rating = rating;
        this.id = new ProductReviewID(product.getId(),customer.getId());
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", rating=" + rating +
                '}';
    }
}
