package com.bootcamp.co;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationSaveCO {
    private UUID productId;

    @NotNull(message = "{validations.product.variation.quantity.required}")
    @PositiveOrZero(message = "{validations.product.variation.quantity.pattern}")
    private BigInteger quantity;

    @NotNull(message = "{validations.product.variation.price.required}")
    @PositiveOrZero(message = "{validations.product.variation.price.pattern}")
    private BigDecimal price;

        @NotNull(message = "{validations.product.variation.metaData.required}")
        private TreeMap<
                @NotBlank(message = "{validations.product.variation.invalid.metadata.field")
                        String,
                @NotBlank(message = "{validations.product.variation.invalid.metadata.value}")
                        String
                > metaData = new TreeMap<>();

    @NotNull(message = "{validations.product.variation.primaryImage.required}")
    private MultipartFile primaryImage;
    private MultipartFile[] secondaryImage= {};
}
