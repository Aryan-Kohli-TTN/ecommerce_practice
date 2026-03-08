package com.bootcamp.service.impl;

import com.bootcamp.co.CategoryFieldValuesListCO;
import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.category.CategoryField;
import com.bootcamp.entity.category.CategoryFieldValues;
import com.bootcamp.entity.category.CategoryFieldValuesId;
import com.bootcamp.exception.category.*;
import com.bootcamp.repository.category.CategoryFieldRepository;
import com.bootcamp.repository.category.CategoryFieldValuesRepository;
import com.bootcamp.repository.category.CategoryRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.CategoryFieldValuesService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

@Service
@AllArgsConstructor
public class CategoryFieldValuesServiceImpl implements CategoryFieldValuesService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryFieldValuesServiceImpl.class);
    private final CategoryFieldValuesRepository categoryFieldValuesRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryFieldRepository categoryFieldRepository;
    private final MessageSource messageSource;

    private void validateCategoryFieldId(UUID id) {
        boolean exists = categoryFieldRepository.existsById(id);
        if (!exists) {
            logger.error("Invalid category field id");
            throw new InvalidCategoryFieldIdException(id);
        }
        logger.info("Category field id successfully validated");
    }

    private void validateCategoryFieldValuesNotExist(UUID categoryId, UUID fieldId) {
        CategoryFieldValuesId categoryFieldValuesId = new CategoryFieldValuesId(fieldId, categoryId);
        if (categoryFieldValuesRepository.existsById(categoryFieldValuesId)) {
            logger.error("category field values exist");
            throw new CategoryFieldValuesAlreadyExistException(categoryId, fieldId);
        }
        logger.info("category field values validated, does not exist previously");
    }

    private void validateCategoryFieldValuesMustExist(UUID categoryId, UUID fieldId) {
        CategoryFieldValuesId categoryFieldValuesId = new CategoryFieldValuesId(fieldId, categoryId);
        if (!categoryFieldValuesRepository.existsById(categoryFieldValuesId)) {
            logger.error("category field values does not found");
            throw new CategoryFieldValuesNotFoundException(fieldId);
        }
        logger.info("category field values are present");
    }

    private void validateUniqueCount(List<String> values, UUID fieldId) {
        List<String> lowerCaseList = values.stream().map(String::toLowerCase).toList();
//        logger.info("{}",lowerCaseList);
        Map<String, Integer> values_count = new HashMap<>();
        lowerCaseList.forEach((val) -> {
            values_count.put(val, values_count.getOrDefault(val, 0) + 1);
        });
        List<String> duplicateValues = new ArrayList<>();
        for (String key : values_count.keySet()) {
            if (values_count.get(key) > 1) {
                duplicateValues.add(key);
            }
        }
        logger.info(duplicateValues.toString());
        int size = duplicateValues.size();
        logger.info("duplicates value size : {}", size);
        if (size > 0) {
            logger.error("fieldId {} have duplicate values ", fieldId);
            throw new DuplicateMetaDataValuesException(duplicateValues, fieldId);
        }
        logger.info("unique count validated");
    }

    private void validateMinCountValues(List<String> values, UUID id) {
        if (values.isEmpty()) {
            logger.error("fieldId {} has zero values", id);
            throw new MetadataMinCountException(id);
        }
    }

    private void validatePrevExistMetaDataWithNew(List<String> values, UUID categoryId, UUID fieldId) {
        CategoryFieldValuesId categoryFieldValuesId = new CategoryFieldValuesId(fieldId, categoryId);
        CategoryFieldValues categoryFieldValues = categoryFieldValuesRepository.findById(categoryFieldValuesId)
                .orElseThrow(()-> new CategoryFieldValuesNotFoundException(fieldId));
        List<String> prevValues = Arrays.stream(categoryFieldValues.getFieldValues().split(",")
                ).map(String::trim)
                .map(String::toLowerCase)
                .toList();
        Map<String, Integer> values_count = new HashMap<>();
        prevValues.forEach((val) -> {
            values_count.put(val, values_count.getOrDefault(val, 0) + 1);
        });
        List<String> lowerCaseList = values.stream().map(String::trim).map(String::toLowerCase).toList();
        // checking that whether is color red already exist and user now also provide again red
        // so for that just storing count
        List<String> duplicateValues = new ArrayList<>();
        lowerCaseList.forEach(val -> {
            if (values_count.containsKey(val)) {
                duplicateValues.add(val);
            }
        });
        if (!duplicateValues.isEmpty()) {
            logger.error("some metaData fields already exist kindly updated");
            throw new SomeMetaFieldsExistException(fieldId, duplicateValues);
        }
        logger.info("metaData validated with previous one");
    }

    private void validateUpdateFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO, UUID categoryId) {
        boolean categoryExists = categoryRepository.existsById(categoryId);
        if(!categoryExists) throw new InvalidCategoryIdException(categoryId);
        categoryFieldValuesListCO.getValuesList().forEach(
                (categoryFieldValuesCO) -> {
                    validateCategoryFieldId(categoryFieldValuesCO.getFieldId());
                    validateCategoryFieldValuesMustExist(categoryId, categoryFieldValuesCO.getFieldId());
                    validateMinCountValues(categoryFieldValuesCO.getValues(), categoryFieldValuesCO.getFieldId());
                    validateUniqueCount(categoryFieldValuesCO.getValues(), categoryFieldValuesCO.getFieldId());

                    validatePrevExistMetaDataWithNew(categoryFieldValuesCO.getValues(), categoryId,
                            categoryFieldValuesCO.getFieldId());
                }
        );
    }

    @Override
    public ApiResponse<Object> updateFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO) {
        UUID categoryId = categoryFieldValuesListCO.getCategoryId();
        validateUpdateFieldValues(categoryFieldValuesListCO, categoryId);
        List<CategoryFieldValues> categoryFieldValuesList = categoryFieldValuesListCO.getValuesList()
                .stream().map((categoryFieldValuesCO) -> {
                    List<String> lowerCaseList = categoryFieldValuesCO.getValues().stream().map(String::trim)
                            .map(String::toLowerCase).toList();
                    String values = String.join(",", lowerCaseList);
                    UUID fieldId = categoryFieldValuesCO.getFieldId();
                    CategoryField categoryField = categoryFieldRepository.findById(fieldId)
                            .orElseThrow(() -> new InvalidCategoryFieldIdException(fieldId));
                    CategoryFieldValuesId categoryFieldValuesId = new CategoryFieldValuesId(fieldId, categoryId);

                    CategoryFieldValues categoryFieldValues = categoryFieldValuesRepository.findById(categoryFieldValuesId).get();
                    categoryFieldValues.setFieldValues(categoryFieldValues.getFieldValues() + "," + values);
                    return categoryFieldValues;
                }).toList();
        categoryFieldValuesRepository.saveAll(categoryFieldValuesList);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("messages.metadata.values.updated", null, locale));
    }

    private HashMap<UUID, Set<String>> getAllParentData(Category parentCategory) {
        if (parentCategory == null) return new HashMap<>();
        Set<CategoryFieldValues> categoryFieldValuesList = parentCategory.getCategoryFieldValuesList();
        HashMap<UUID, Set<String>> parentAllValues = new HashMap<>();
        categoryFieldValuesList.forEach((categoryFieldValue) -> {
            String[] values = categoryFieldValue.getFieldValues().split(",");
            parentAllValues.put(categoryFieldValue.getId().getCategoryFieldId(), new HashSet<>(Arrays.asList(values)));
        });
        logger.info("all parent data successfully fetched");
        return parentAllValues;
    }

    private void validateAddFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO, UUID categoryId) {
        categoryFieldValuesListCO.getValuesList().forEach(
                (categoryFieldValuesCO) -> {
                    validateCategoryFieldId(categoryFieldValuesCO.getFieldId());
                    validateCategoryFieldValuesNotExist(categoryId, categoryFieldValuesCO.getFieldId());
                    // no need of this to check before--> because sometimes i need to copy only my parent one
                    // so checking it later when i fetched parent also
//                    validateMinCountValues(categoryFieldValuesCO.getValues(),categoryFieldValuesCO.getFieldId());
                    validateUniqueCount(categoryFieldValuesCO.getValues(), categoryFieldValuesCO.getFieldId());
                }
        );
    }

    private void combineParentAndChildData(CategoryFieldValuesListCO categoryFieldValuesListCO,
                                           HashMap<UUID, Set<String>> parentAllValues) {

        logger.info("{}", categoryFieldValuesListCO.getValuesList().size());
        categoryFieldValuesListCO.getValuesList().forEach(categoryFieldValuesCO -> {
            UUID fieldID = categoryFieldValuesCO.getFieldId();
            List<String> newValues = categoryFieldValuesCO.getValues().stream().map(String::toLowerCase).toList();
            Set<String> values = parentAllValues.getOrDefault(fieldID, new HashSet<>());
            values.addAll(newValues);
            validateMinCountValues(values.stream().toList(), fieldID);
            parentAllValues.put(fieldID, values);
        });
        logger.info("parent and child field value metaData successfully combined");
    }

    private List<CategoryFieldValues> convertMapToEnityList(HashMap<UUID, Set<String>> parentAllValues, Category category) {
        List<CategoryFieldValues> categoryFieldValuesList = new ArrayList<>();
        for (UUID fieldId : parentAllValues.keySet()) {
            Set<String> values = parentAllValues.get(fieldId);
            CategoryField categoryField = categoryFieldRepository.findById(fieldId)
                    .orElseThrow(() -> new InvalidCategoryFieldIdException(fieldId));
            CategoryFieldValues categoryFieldValues =
                    new CategoryFieldValues(category, categoryField, String.join(",", values));
            categoryFieldValuesList.add(categoryFieldValues);
        }
        return categoryFieldValuesList;
    }

    @Override
    public ApiResponse<Object> addFieldValues(CategoryFieldValuesListCO categoryFieldValuesListCO) {
        UUID categoryId = categoryFieldValuesListCO.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new InvalidCategoryIdException(categoryId));

        validateAddFieldValues(categoryFieldValuesListCO, categoryId);
        HashMap<UUID, Set<String>> parentAllValues = getAllParentData(category.getParentCategory());
        combineParentAndChildData(categoryFieldValuesListCO, parentAllValues);
        List<CategoryFieldValues> categoryFieldValuesList = convertMapToEnityList(parentAllValues, category);
        categoryFieldValuesList = categoryFieldValuesRepository.saveAll(categoryFieldValuesList);
        category.appendCategoryFieldValues(categoryFieldValuesList);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("messages.metadata.values.added", null, locale));
    }
}
