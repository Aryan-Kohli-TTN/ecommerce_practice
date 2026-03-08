package com.bootcamp.EntityTests;


import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.category.CategoryField;
import com.bootcamp.entity.category.CategoryFieldValues;
import com.bootcamp.entity.category.CategoryFieldValuesId;
import com.bootcamp.repository.category.CategoryFieldRepository;
import com.bootcamp.repository.category.CategoryFieldValuesRepository;
import com.bootcamp.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class CategoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryFieldRepository categoryFieldRepository;

    @Autowired
    CategoryFieldValuesRepository categoryFieldValuesRepository;

    @Test
    void test_category_without_parent_save(){
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);
    }

@Test
    void test_category_with_parent_save(){
        Category category = new Category();
        category.setCategoryName("MobilePhone");
        category.setParentCategory(categoryRepository.findByCategoryName("Electronics").get());
        categoryRepository.save(category);
    }

    @Test
    void test_category_metadata_field_save(){

        for(int i=0;i<5;i++){
            CategoryField categoryField = new CategoryField();
            categoryField.setCategoryFieldName("");
            categoryFieldRepository.save(categoryField);
        }
    }

    @Test
    void test_add_metadata_field_values(){
        Optional<Category> category = categoryRepository.findByCategoryName("MobilePhone");
        for(int i=0;i<5;i++){
            CategoryField categoryField = categoryFieldRepository
                    .findByCategoryFieldName("field"+i);
            CategoryFieldValues categoryFieldValues =new CategoryFieldValues();
            categoryFieldValues.setCategory(category.get());
            categoryFieldValues.setCategoryField(categoryField);
            categoryFieldValues.setFieldValues("L,XL,XXL");
            categoryFieldValues.setId(new CategoryFieldValuesId(category.get().getId(), categoryField.getId()));
            categoryFieldValuesRepository.save(categoryFieldValues);
        }

    }
}
