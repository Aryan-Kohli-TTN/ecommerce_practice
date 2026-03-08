package com.bootcamp.dto;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.user.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO
{
    private UUID productId;
    private String productName;
    private String productDescription;
    private String productBrand;
    private boolean isCancellable;
    private boolean isReturnable;
    private CategoryDTO category;

}
