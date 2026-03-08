package com.bootcamp.co;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFieldCO {
    @NotBlank(message = "{validations.category.field.name.required}")
    @Pattern(regexp = "^(?=.{3,40}$)[A-Za-z]+(?:[\\s-][A-Za-z]+)*$", message = "{validations.category.field.name.pattern}")
    private String categoryFieldName;
}
