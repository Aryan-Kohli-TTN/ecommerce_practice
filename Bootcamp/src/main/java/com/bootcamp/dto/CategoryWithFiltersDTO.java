package com.bootcamp.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryWithFiltersDTO {
    private UUID categoryId;
    private String categoryName;
    private UUID parentCategoryId;
    private List<CategoryFieldValueDTO> metaData;
    private List<String> brands;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
