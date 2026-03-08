package com.bootcamp.dto;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryWithChildrensDTO {
    private UUID id;
    private String categoryName;
    private List<CategoryDTO> subCategories;
}
