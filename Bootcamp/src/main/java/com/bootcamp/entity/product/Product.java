package com.bootcamp.entity.product;


import com.bootcamp.auditing.Auditing;
import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.user.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Builder
@SQLDelete(sql = "update product SET is_deleted=true where product_id =?")
@SQLRestriction(value = "is_deleted=false")
public class Product {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "product_name",nullable = false)
    private String productName;

    @Column(name = "product_description",nullable = false)
    private String productDescription;

    @Column(name = "product_brand",nullable = false)
    private String productBrand;

    @Column(name = "is_cancellable",nullable = false)
    private boolean isCancellable;

    @Column(name = "is_returnable",nullable = false)
    private boolean isReturnable;

    @Column(name = "is_active",nullable = false)
    private boolean isActive;

    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "seller_user_id",nullable = false)
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @Embedded
    Auditing auditing;

    @OneToMany(mappedBy = "product")
    Set<ProductVariation> productVariationList=new HashSet<>();

    @OneToMany(mappedBy = "product")
    Set<ProductReview> productReviewsList=new HashSet<>();

    public Product(String productName, String productDescription, String productBrand, boolean isCancellable, boolean isReturnable, boolean isActive, boolean isDeleted, Seller seller,Category category ,  Set<ProductVariation> productVariationList,Set<ProductReview> productReviewsList) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productBrand = productBrand;
        this.isCancellable = isCancellable;
        this.isReturnable = isReturnable;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.seller = seller;
        this.category=category;
        this.productVariationList=productVariationList;
        this.productReviewsList=productReviewsList;
    }
    public void addProductVariation(ProductVariation productVariation){
        productVariationList.add(productVariation);
    }
    public void addProductReview(ProductReview productReview){
        productReviewsList.add(productReview);
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", isCancellable=" + isCancellable +
                ", isReturnable=" + isReturnable +
                ", isActive=" + isActive +
                ", isDeleted=" + isDeleted +
                ", sellerId=" + seller.getId() +
                ", categoryId=" + category.getId() +
                ", productVariation count=" + productVariationList.size()+
                ", productReviews count=" + productReviewsList.size() +
                '}';
    }
}
