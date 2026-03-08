package com.bootcamp.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductWithVariationDTO
{
    private UUID productId;
    private String productName;
    private String productDescription;
    private String productBrand;
    private boolean isCancellable;
    private boolean isReturnable;
    private CategoryDTO category;
}
