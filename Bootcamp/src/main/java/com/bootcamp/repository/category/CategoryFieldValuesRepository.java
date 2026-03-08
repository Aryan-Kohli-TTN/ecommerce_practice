package com.bootcamp.repository.category;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.category.CategoryFieldValues;
import com.bootcamp.entity.category.CategoryFieldValuesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CategoryFieldValuesRepository extends JpaRepository<CategoryFieldValues, CategoryFieldValuesId> {

    List<CategoryFieldValues> findByCategory(Category category);
}
