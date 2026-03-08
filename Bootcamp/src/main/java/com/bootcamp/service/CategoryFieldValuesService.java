package com.bootcamp.service;

import com.bootcamp.co.CategoryFieldValuesListCO;
import com.bootcamp.response.ApiResponse;

public interface CategoryFieldValuesService {
    ApiResponse<Object> updateFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO);

    ApiResponse<Object> addFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO);
}
