package com.bootcamp.repository.product;

import java.util.List;

public interface CustomRepository {
    List<Object[]> executeQuery(String query);
}
