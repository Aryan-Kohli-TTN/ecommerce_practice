package com.bootcamp.entity.user;

import com.bootcamp.entity.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@DiscriminatorValue("seller")
public class Seller extends User {

    @Column(name = "gst_no",length = 15)
    private String gstNo;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_contact_number")
    private String companyContactNumber;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonBackReference
    Set<Product> productList = new HashSet<>();

    public void addProduct(Product product){
        productList.add(product);
    }
    @Override
    public String toString() {
        return super.toString()+"Seller{" +
                "gstNo='" + gstNo + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyContactNumber='" + companyContactNumber + '\'' +
                ", Products Count='" + productList.size() + '\'' +
                '}';
    }
}
