package com.bootcamp.dto;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDTO {
    private UUID id;
    private String categoryName;
    private UUID parentCategoryId;
    private List<CategoryDTO> parentChain;
    private List<CategoryDTO> subCategories;
    private List<CategoryFieldValueDTO> FieldValuesList;
}
