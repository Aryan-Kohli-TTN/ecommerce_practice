package com.bootcamp.co;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationUpdateCO {
    @PositiveOrZero(message = "{validations.product.variation.quantity.pattern}")
    private BigInteger productVariationQuantity;
    @PositiveOrZero(message = "{validations.product.variation.price.pattern}")
    private BigDecimal productVariationPrice;
        private TreeMap<
                @NotBlank(message = "{validations.product.variation.invalid.metadata.field")
                        String,
                @NotBlank(message = "{validations.product.variation.invalid.metadata.value}")
                        String
                > metaData = new TreeMap<>();
    private MultipartFile primaryImage;
    private Boolean isActive;
    private MultipartFile[] secondaryImage= {};
}
