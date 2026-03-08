package com.bootcamp.repository.category;

import com.bootcamp.entity.category.CategoryField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
@Repository
public interface CategoryFieldRepository extends JpaRepository<CategoryField, UUID> {

     CategoryField findByCategoryFieldName(String categoryFieldName);
     boolean existsByCategoryFieldName(String categoryFieldName);
     Page<CategoryField> findByCategoryFieldNameContaining(String categoryFieldName, Pageable pageable);
     List<CategoryField> findByCategoryFieldNameIn(Set<String> categoryFieldNames);

}
