package com.bootcamp.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
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
public class ProductVariationDTO {
    private UUID id;
    private BigInteger productVariationQuantity;
    private BigDecimal productVariationPrice;
    private JsonNode metaData;
    private String primaryImage;
    private List<String> secondaryImages;
}
