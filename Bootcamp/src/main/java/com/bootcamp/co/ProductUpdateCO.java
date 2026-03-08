package com.bootcamp.co;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductUpdateCO {
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]{3,100}$", message = "{validations.productName.pattern}")
    private String productName;
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]{3,50}$", message = "{validations.productBrand.pattern}")
    private String productBrand;
    @Size(max = 300,message = "{validations.productDescription.pattern}")
    private String productDescription;
    private Boolean isCancellable;
    private Boolean isReturnable;
}
