package com.bootcamp.entity.product;


import com.bootcamp.auditing.Auditing;
import com.bootcamp.entity.order.OrderProduct;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@SQLDelete(sql = "update product_variation SET is_deleted=true where product_variation_id =?")
@SQLRestriction(value = "is_deleted=false")
@Builder
public class ProductVariation {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "product_variation_id")
    private UUID id;


//    @Column(name = "product_variation_primary_image_name",nullable = false)
//    private String productPrimaryImageName;


    @Column(name = "product_variation_quantity",nullable = false)
    private BigInteger productVariationQuantity;


    @Column(name = "product_variation_price",nullable = false)
    private BigDecimal productVariationPrice;

    @Column(name = "is_active",nullable = false)
    private boolean isActive;

    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @Column(name = "meta_data",columnDefinition = "JSON")
    String metaData;

    @Embedded
    Auditing auditing;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)

    Set<OrderProduct> orderProductList= new HashSet<>();

//    @OneToMany(mappedBy = "productVariation",cascade = CascadeType.ALL)
//    Set<Cart> addedByCustomersInCart= new HashSet<>();

    public void addOrderProduct(OrderProduct orderProduct){
        orderProductList.add(orderProduct);
    }
//    public void addAddedByCustomersInCart(Cart cart)
//    {
//        addedByCustomersInCart.add(cart);
//    }
    @Override
    public String toString() {
        return "ProductVariation{" +
                "id=" + id +
                ", productVariationQuantity=" + productVariationQuantity +
                ", productVariationPrice=" + productVariationPrice +
                ", isActive=" + isActive +
                ", productId=" + product.getId() +
                ", metaData='" + metaData + '\'' +
                ", orderProduct count=" + orderProductList.size() +
//                ", addedByCustomersInCart count=" + addedByCustomersInCart.size() +
                '}';
    }
}
