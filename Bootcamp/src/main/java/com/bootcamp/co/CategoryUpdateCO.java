package com.bootcamp.co;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateCO {
    @NotBlank(message = "{validations.category.name.required}")
    @Pattern(message = "{validations.category.name.pattern}",
            regexp = "^[A-Za-z]+(?:[\\s-'][A-Za-z]+)*$")
    private String categoryName;
}
