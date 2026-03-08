package com.bootcamp.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductVariationListDTO {
    private ProductDTO productInfo;
    private List<ProductVariationDTO> variations;
    public void addProductVariationDto(ProductVariationDTO productVariationDTO){
        if(variations==null){
            variations=new ArrayList<>();
        }
        variations.add(productVariationDTO);
    }
}
