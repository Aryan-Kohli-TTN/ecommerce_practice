package com.bootcamp.service;

import com.bootcamp.co.ProductSaveCO;
import com.bootcamp.co.ProductUpdateCO;
import com.bootcamp.co.ProductVariationSaveCO;
import com.bootcamp.co.ProductVariationUpdateCO;
import com.bootcamp.response.ApiResponse;

import java.util.Map;
import java.util.UUID;

public interface ProductService {
    ApiResponse<Object> saveProduct(ProductSaveCO productSaveCO);

    ApiResponse<Object> saveProductVariation(ProductVariationSaveCO productVariationSaveCO);


    ApiResponse<Object> getAllProducts(Map<String,String> allParams);

    ApiResponse<Object> getOneProduct(String id);

    ApiResponse<Object> getAllProductVariation(Map<String,String> allParams);

    ApiResponse<Object> getOneProductVariation(String id);

    ApiResponse<Object> updateProduct(String productId , ProductUpdateCO productUpdateCO );

    ApiResponse<Object> updateProductVariation(String productVariationId, ProductVariationUpdateCO productVariationUpdateCO);

    ApiResponse<Object> deleteProduct(String productId);

    ApiResponse<Object> activateProduct(String productId);

    ApiResponse<Object> deactivateProduct(String productId);


    ApiResponse<Object> getSimilarProduct(String productId,String Size, String Offset, String orderBy, String sortBy);
}
