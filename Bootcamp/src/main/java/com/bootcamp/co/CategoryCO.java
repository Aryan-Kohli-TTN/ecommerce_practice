package com.bootcamp.co;

import com.bootcamp.entity.category.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCO {
    @NotBlank(message = "{validations.category.name.required}")
    @Pattern(message = "{validations.category.name.pattern}",
            regexp = "^(?=.{3,40}$)[A-Za-z]+(?:[\\s-'][A-Za-z]+)*$")
    private String categoryName;
    private String parentCategoryId;
}
