package com.bootcamp.repository.category;

import com.bootcamp.entity.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByCategoryName(String categoryName);
    boolean existsByCategoryNameAndParentCategoryIsNull(String categoryName);
    List<Category> findByParentCategory(Category parentCategory);

    @Query("SELECT c from Category c where c.parentCategory IS NULL")
    List<Category> getAllRootCategory(Pageable pageable);

@EntityGraph(attributePaths = {
        "parentCategory",
        "productList",
        "categoryFieldValuesList"
})
@Query("SELECT c FROM Category c WHERE c.id = :id")
        Optional<Category> getCategoryWithDetails(@Param("id")UUID id);


    @EntityGraph(attributePaths = {
            "parentCategory",
            "productList",
            "categoryFieldValuesList"
    })
    @Query("SELECT c FROM Category c")
    List<Category> getAllCategory(Pageable pageable);

    @EntityGraph(attributePaths = {
            "parentCategory",
            "productList",
            "categoryFieldValuesList"
    })
    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE CONCAT('%', :categoryName, '%')")
    List<Category> getAllCategoryWithName(@Param("categoryName") String categoryName, Pageable pageable);

    @EntityGraph(attributePaths = {
            "parentCategory",
            "productList",
            "categoryFieldValuesList"
    })
    @Query("Select c FROM Category c WHERE c.isLeafNode=true and (:categoryName='' OR c.categoryName LIKE CONCAT('%', :categoryName, '%'))")
    List<Category> getAllLeafCategory(@Param("categoryName")String categoryName,Pageable pageable);

}
