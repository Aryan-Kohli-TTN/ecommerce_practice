package com.bootcamp.service;

import com.bootcamp.co.CategoryFieldCO;
import com.bootcamp.response.ApiResponse;

import java.util.List;

public interface CategoryFieldService {
    ApiResponse<Object> addCategoryField(CategoryFieldCO categoryFieldCO);

    ApiResponse<Object> addManyCategoryField(List<CategoryFieldCO> categoryFieldCOList);

    ApiResponse<Object> getAllCategoryField(String pageSize, String pageOffset, String sortBy, String orderBy, String name);
}
