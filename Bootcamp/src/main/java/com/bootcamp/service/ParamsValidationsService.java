package com.bootcamp.service;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface ParamsValidationsService {
    int getPageSize(String pageSize);

    int getPageOffset(String pageOffset);

    Sort.Direction getOrderBy(String orderBy);

    String getOrderByString(String orderBy);

    String getSortByForProduct(String sortBy);

    String getSortBy(String sortBy, List<String> validValues);
}
