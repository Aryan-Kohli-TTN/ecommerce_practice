package com.bootcamp.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductVariationWithProductDTO {
    private ProductDTO product;
    private ProductVariationDTO productVariation;
}
