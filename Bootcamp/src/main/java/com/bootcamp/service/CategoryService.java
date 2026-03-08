package com.bootcamp.service;

import com.bootcamp.co.CategoryCO;
import com.bootcamp.co.CategoryUpdateCO;
import com.bootcamp.response.ApiResponse;

import java.util.UUID;

public interface CategoryService {
    ApiResponse<Object> addCategory(CategoryCO categoryCO);

    ApiResponse<Object> getAllCategory(String Size, String Offset, String orderBy, String sortBy, String categoryName);

    ApiResponse<Object> getOneCategory(String id);

    ApiResponse<Object> updateCategory(String id, CategoryUpdateCO categoryUpdateCO);

    ApiResponse<Object> getCategoryInfo(String categoryId);
}
