package com.bootcamp.co;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.product.ProductReview;
import com.bootcamp.entity.product.ProductVariation;
import com.bootcamp.entity.user.Seller;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductSaveCO {
    @NotBlank(message = "{validations.productName.required}")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]{3,100}$", message = "{validations.productName.pattern}")
    private String productName;
    @Size(max = 300,message = "{validations.productDescription.pattern}")
    @NotBlank(message = "{validations.productDescription.required}")
    private String productDescription;
    @NotBlank(message = "{validations.productBrand.required}")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]{3,50}$", message = "{validations.productBrand.pattern}")
    private String productBrand;
    private boolean isCancellable=false;
    private boolean isReturnable=false;
    @NotNull(message = "{validations.categoryId.required}")
    private UUID  categoryId;
}
