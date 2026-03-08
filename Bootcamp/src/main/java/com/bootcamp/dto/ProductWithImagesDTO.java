package com.bootcamp.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductWithImagesDTO
{
    private UUID productId;
    private String productName;
    private String productDescription;
    private String productBrand;
    private boolean isCancellable;
    private boolean isReturnable;
    private CategoryDTO category;
    private List<String> images;
}
