package com.bootcamp.entity.category;


import com.bootcamp.auditing.Auditing;
import com.bootcamp.entity.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Builder
public class Category {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "category_id")
    private UUID id;

    @Column(name = "category_name",nullable = false)
    private String categoryName;

    @Embedded
    Auditing auditing;

    @Column(name = "is_leaf_node")
    private boolean isLeafNode;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    @JsonIgnore
    private Category parentCategory;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<CategoryFieldValues> categoryFieldValuesList =new HashSet<>();

    @OneToMany(mappedBy = "category",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Product> productList = new HashSet<>();

    public Category(String categoryName, Category parentCategory ){
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.auditing=new Auditing();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", parentCategoryId=" + (parentCategory!=null?parentCategory.getId():null) +
                ", categoryFieldValuesList Count=" + categoryFieldValuesList.size() +
                ", productList Count=" + productList.size() +
                '}';
    }

    public void addCategoryFieldValues(CategoryFieldValues categoryFieldValues){
        categoryFieldValuesList.add(categoryFieldValues);
    }
    public void appendCategoryFieldValues(List<CategoryFieldValues> categoryFieldValues){
        categoryFieldValuesList.addAll(categoryFieldValues);
    }

    public void addProduct(Product product){
        productList.add(product);
    }

}
