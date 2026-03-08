package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.co.CategoryFieldCO;
import com.bootcamp.dto.CategoryFieldDTO;
import com.bootcamp.entity.category.CategoryField;
import com.bootcamp.exception.category.CategoryFieldAlreadyExistException;
import com.bootcamp.repository.category.CategoryFieldRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.CategoryFieldService;
import com.bootcamp.service.ParamsValidationsService;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;

@Service
@AllArgsConstructor
public class CategoryFieldServiceImpl implements CategoryFieldService {
    private static  final Logger logger = LoggerFactory.getLogger(CategoryFieldServiceImpl.class);
    private final CategoryFieldRepository categoryFieldRepository;
    private final ParamsValidationsService paramsValidationsService;

    private CategoryFieldDTO addOneCategoryField(CategoryFieldCO categoryFieldCO){

        CategoryField categoryField = new CategoryField();
        categoryField.setAuditing(new Auditing());
        categoryField.setCategoryFieldName(categoryFieldCO.getCategoryFieldName().toLowerCase());
        categoryField = categoryFieldRepository.save(categoryField);
        CategoryFieldDTO categoryFieldDTO = new CategoryFieldDTO();
        BeanUtils.copyProperties(categoryField,categoryFieldDTO);
        logger.info("Category field successfully added name: {}",categoryFieldCO.getCategoryFieldName());
        return categoryFieldDTO;
    }
    @Override
    public ApiResponse<Object> addCategoryField(CategoryFieldCO categoryFieldCO){
        validateCategoryFieldNameExist(categoryFieldCO.getCategoryFieldName());
        logger.info("Category fields validated");
        return ResponseUtil.okWithData(addOneCategoryField(categoryFieldCO));
    }

    private  void validateCategoryFieldNameExist(String categoryFieldName)
    {
        if(categoryFieldRepository.existsByCategoryFieldName(categoryFieldName.toLowerCase()))
        {
            logger.error("category field already exist");
            throw new CategoryFieldAlreadyExistException();
        }
    }
    @Override
    public ApiResponse<Object> addManyCategoryField(List<CategoryFieldCO> categoryFieldCOList){
        categoryFieldCOList.forEach((categoryFieldCO ->
                validateCategoryFieldNameExist(categoryFieldCO.getCategoryFieldName())));
        logger.info("All category fields validated");
        List<CategoryFieldDTO> categoryFieldDTOS= new ArrayList<>();
        categoryFieldCOList.forEach(categoryFieldCO -> {
            categoryFieldDTOS.add(addOneCategoryField(categoryFieldCO));
        });
        return ResponseUtil.okWithData(categoryFieldDTOS);
    }
    private List<CategoryFieldDTO> convertToDtoList(List<CategoryField> categoryFieldList){
        List<CategoryFieldDTO> categoryFieldDTOS = new ArrayList<>();
        logger.info("converting categoryFieldList to CategoryfieldDtoList");
        categoryFieldList.forEach(categoryField -> {
            CategoryFieldDTO categoryFieldDTO = new CategoryFieldDTO();
            BeanUtils.copyProperties(categoryField,categoryFieldDTO);
            categoryFieldDTOS.add(categoryFieldDTO);
        });
        return categoryFieldDTOS;
    }
    @Override
    public ApiResponse<Object> getAllCategoryField(String pageSize, String pageOffset, String sortBy, String orderBy, String name){
        int size= paramsValidationsService.getPageSize(pageSize);
        int offset=paramsValidationsService.getPageOffset(pageOffset);
        Sort.Direction direction= paramsValidationsService.getOrderBy(orderBy);
        String sort_by = paramsValidationsService.getSortBy(sortBy, Arrays.asList("id","createdAt","auditing.createdAt"
                ,"categoryFieldName"));
        logger.info("all params successfully validated");
        PageRequest pageRequest = PageRequest.of(offset,size, Sort.by(direction,sort_by));
        List<CategoryField> categoryFieldList =
                categoryFieldRepository.findByCategoryFieldNameContaining(name,pageRequest).getContent();
        logger.info("category field successfully fetched");
        return ResponseUtil.okWithData(convertToDtoList(categoryFieldList));
    }
}
