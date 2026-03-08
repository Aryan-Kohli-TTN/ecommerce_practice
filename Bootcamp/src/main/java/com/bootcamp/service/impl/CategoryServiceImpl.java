package com.bootcamp.service.impl;


import com.bootcamp.auditing.Auditing;
import com.bootcamp.co.CategoryCO;
import com.bootcamp.dto.CategoryFieldValueDTO;
import com.bootcamp.co.CategoryUpdateCO;
import com.bootcamp.dto.CategoryDTO;
import com.bootcamp.dto.CategoryTreeDTO;
import com.bootcamp.dto.CategoryWithChildrensDTO;
import com.bootcamp.dto.CategoryWithFiltersDTO;
import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.category.CategoryFieldValues;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.category.*;
import com.bootcamp.exception.product.CategoryIsRequiredException;
import com.bootcamp.exception.product.NoProductFoundException;
import com.bootcamp.repository.category.CategoryFieldValuesRepository;
import com.bootcamp.repository.category.CategoryRepository;
import com.bootcamp.repository.product.ProductRepository;
import com.bootcamp.repository.product.ProductVariationRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.CategoryService;
import com.bootcamp.service.ParamsValidationsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;
    private final ParamsValidationsService paramsValidationsService;
    private final ProductVariationRepository productVariationRepository;
    private final ProductRepository productRepository;
    private final CategoryFieldValuesRepository categoryFieldValuesRepository;


    private UUID stringtoUUIDParentCategory(String id){
        if(id==null) return null;
        UUID catId;
        try{
            catId= UUID.fromString(id);
        } catch (Exception e) {
            logger.error("invalid uuid format for parentCategoryId");
            throw new InvalidParentCategoryIdException();
        }
        return catId;
    }
    private UUID stringtoUUIDCategory(String id){
        if(id==null) return null;
        UUID catId=null;
        try{
            catId= UUID.fromString(id);
        } catch (Exception e) {
            logger.error("invalid uuid format for CategoryId");
            throw new InvalidCategoryIdException(null);
        }
        return catId;
    }
    @Override
    public ApiResponse<Object> addCategory(CategoryCO categoryCO) {
        UUID parentCatId=stringtoUUIDParentCategory(categoryCO.getParentCategoryId());
        if (parentCatId == null) {
            logger.info("Adding category as root");
            return addCategoryAsRoot(categoryCO, null);
        } else {
            logger.info("Adding category as non-root");
            return addCategoryAsNonRoot(categoryCO,parentCatId);
        }
    }

    private ApiResponse<Object> addCategoryAsRoot(CategoryCO categoryCO,UUID parentCatId) {
        boolean categoryExist = categoryRepository.existsByCategoryNameAndParentCategoryIsNull(categoryCO.getCategoryName());
        if (categoryExist) {
            logger.error("root category already exist with same name");
            throw new RootCategoryAlreadyExistException();
        }
        Category category = Category.builder().parentCategory(null).categoryName(categoryCO.getCategoryName().toLowerCase())
                .auditing(new Auditing()).isLeafNode(true).build();
        category = categoryRepository.save(category);
        CategoryDTO categoryDTO = CategoryDTO.builder().parentCategoryId(parentCatId)
                .id(category.getId()).categoryName(category.getCategoryName()).build();
        logger.info("category successfully added at root");
        return ResponseUtil.okWithData(categoryDTO);
    }
    private ApiResponse<Object> addCategoryAsNonRoot(CategoryCO categoryCO,UUID parentCatId) {
        Category parentCategory = categoryRepository.findById(parentCatId)
                .orElseThrow(InvalidParentCategoryIdException::new);
        validateNoProductList(parentCategory);
        validateUniqueAtBreadth(parentCategory, categoryCO.getCategoryName());
        validateParentsMatch(parentCategory, categoryCO.getCategoryName());
        logger.info("All validations done for adding category as root");
        Category category = Category.builder().categoryName(categoryCO.getCategoryName().toLowerCase())
                .parentCategory(parentCategory).auditing(new Auditing()).isLeafNode(true).build();
        category = categoryRepository.save(category);
        if(parentCategory.isLeafNode()){
            parentCategory.setLeafNode(false);
            categoryRepository.save(parentCategory);
        }
        CategoryDTO categoryDTO = CategoryDTO.builder().parentCategoryId(parentCatId)
                .id(category.getId()).categoryName(category.getCategoryName()).build();
        logger.info("category successfully saved as non-root");
        return ResponseUtil.okWithData(categoryDTO);
    }

    private void validateParentsMatch(Category parentCategory, String categoryName) {
        while (parentCategory != null) {
            if (parentCategory.getCategoryName().equalsIgnoreCase(categoryName)) {
                logger.error("category has same name in one of its parent chain");
                throw new CategoryExistAtDepthException();
            } else {
                parentCategory = parentCategory.getParentCategory();
            }
        }
        logger.info("category name successfully validated with parent chain");
    }

    private void validateNoProductList(Category parentCategory) {
        if (!parentCategory.getProductList().isEmpty()) {
            logger.error("parent category has products");
            throw new ParentCategoryHasProductException();
        }
    }

    private void validateUniqueAtBreadth(Category parentCategory, String categoryName) {
        List<Category> parentCategoryOtherChilds = categoryRepository.findByParentCategory(parentCategory);
        parentCategoryOtherChilds.forEach(parentCategoryOtherChild -> {
            if (parentCategoryOtherChild.getCategoryName().equalsIgnoreCase(categoryName)) {
                logger.error("category name exist in siblings");
                throw new CategoryExistAtWidthException();
            }
        });
        logger.info("category name successfully validated at width/breadth");
    }

    private CategoryDTO convertToCategoryDTO(Category category) {
        logger.info("converting category to categoryDTO");
        return new CategoryDTO(category.getId(),
                category.getCategoryName(), category.getParentCategory() != null ? category.getParentCategory().getId() : null);
    }
    private List<CategoryFieldValueDTO> convertToFieldValueListDTO(Set<CategoryFieldValues> categoryFieldValues){
        logger.info("converting Set of CategoryFieldValues to list of CategoryFieldValuesDTO");
         return categoryFieldValues.stream().map(categoryFieldValue->
                CategoryFieldValueDTO.builder()
                        .fieldId(categoryFieldValue.getId().getCategoryFieldId())
                        .values(categoryFieldValue.getFieldValues()).build()
         ).toList();
    }
    private List<CategoryFieldValueDTO> convertToFieldValueListDTO(List<CategoryFieldValues> categoryFieldValues){
        logger.info("converting list of CategoryFieldValues to list of CategoryFieldValuesDTO");
        return categoryFieldValues.stream().map(categoryFieldValue->
                CategoryFieldValueDTO.builder()
                        .fieldId(categoryFieldValue.getId().getCategoryFieldId())
                        .values(categoryFieldValue.getFieldValues()).build()
         ).toList();
    }
    private CategoryTreeDTO convertToCategoryTreeDTO(Category category) {
        logger.info("converting category to categoryTreeDTO");
        return CategoryTreeDTO.builder()
                .categoryName(category.getCategoryName())
                .id(category.getId())
                .subCategories(getSubCategories(category)).parentChain(getParentChain(category))
                .parentCategoryId(category.getParentCategory() == null ? null : category.getParentCategory().getId())
                .FieldValuesList(convertToFieldValueListDTO(category.getCategoryFieldValuesList()))
                .build();
    }

    private List<CategoryTreeDTO> convertToCategoryTreeDTOList(List<Category> categoryList) {
        logger.info("converting category-List to categoryTreeDTO-List");
        return categoryList.stream().map(this::convertToCategoryTreeDTO).toList();
    }
    private ApiResponse<Object> getAllCategoryForAdmin(Pageable pageable,String categoryName){
        List<Category> categories = categoryRepository.getAllCategoryWithName(categoryName,pageable);
        logger.info("All categories fetched for admin");
        List<CategoryTreeDTO> categoryTreeDTOList = convertToCategoryTreeDTOList(categories);
        return ResponseUtil.okWithData(categoryTreeDTOList);
    }
    private ApiResponse<Object> getAllLeafCategoryForSeller(Pageable pageable,String categoryName){
        List<Category> categories = categoryRepository.getAllLeafCategory(categoryName,pageable);
        logger.info("All leaf categories fetched for seller");
        List<CategoryTreeDTO> categoryTreeDTOList = convertToCategoryTreeDTOList(categories);
        return ResponseUtil.okWithData(categoryTreeDTOList);
    }
    private ApiResponse<Object> getAllRootCategoryForCustomer(Pageable pageable){
        List<Category> categories = categoryRepository.getAllRootCategory(pageable);
        List<CategoryTreeDTO> categoryTreeDTOList = convertToCategoryTreeDTOList(categories);
        logger.info("All root categories fetched for customer");
        return ResponseUtil.okWithData(categoryTreeDTOList);
    }


    // here for querying only categoryName is present so for keeping simple not taking Map<String,String>
    @Override
    public ApiResponse<Object> getAllCategory(String Size, String Offset, String orderBy, String sortBy, String categoryName) {
        int pageSize = paramsValidationsService.getPageSize(Size);
        int pageOffset = paramsValidationsService.getPageOffset(Offset);
        sortBy = paramsValidationsService.getSortBy(sortBy, Arrays.asList("id", "categoryName",
                "createdAt", "updatedAt", "auditing.createdAt", "auditing.updatedAt"));

        Sort.Direction direction = paramsValidationsService.getOrderBy(orderBy);
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(direction, sortBy));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
        logger.info("All params inputs successfully validated");
        if (authority.equals(Authority.ROLE_ADMIN)) {
            return getAllCategoryForAdmin(pageable,categoryName.trim());
        }
        else if(authority.equals(Authority.ROLE_SELLER)){
            return getAllLeafCategoryForSeller(pageable,categoryName.trim());
        }
        else{
            return getAllRootCategoryForCustomer(pageable);
        }
    }

    private List<CategoryDTO> getParentChain(Category category) {
        List<CategoryDTO> parentChain = new ArrayList<>();
        Category parent = category.getParentCategory();
        while (parent != null) {
            logger.info("In parent chain for parent : {}", parent.getCategoryName());
            parentChain.add(convertToCategoryDTO(parent));
            parent = parent.getParentCategory();
        }
        return parentChain;
    }

    private List<CategoryDTO> getSubCategories(Category category) {
        List<CategoryDTO> subCategories = new ArrayList<>();
        List<Category> childCategories = categoryRepository.findByParentCategory(category);
        logger.info("Getting child categories childCategoriesSize: {}", childCategories.size());
        childCategories.forEach(childCategory -> {
            subCategories.add(convertToCategoryDTO(childCategory));
        });
        return subCategories;
    }
    private ApiResponse<Object> getOneCategoryForAdmin(Category category){
        logger.info("getting one category for admin");
        CategoryTreeDTO categoryTreeDTO = CategoryTreeDTO.builder()
                .categoryName(category.getCategoryName())
                .id(category.getId()).parentChain(getParentChain(category))
                .subCategories(getSubCategories(category))
                .parentCategoryId(category.getParentCategory() == null ? null : category.getParentCategory().getId())
                .FieldValuesList(convertToFieldValueListDTO(category.getCategoryFieldValuesList()))
                .build();
        return ResponseUtil.okWithData(categoryTreeDTO);
    }
    private ApiResponse<Object> getOneCategoryForCustomerNoFilters(Category category){
        logger.info("getting one category for customer no filters , children categories info");
        CategoryWithChildrensDTO categoryWithChildrensDTO = CategoryWithChildrensDTO.builder()
                .categoryName(category.getCategoryName())
                .id(category.getId())
                .subCategories(getSubCategories(category))
                .build();
        return ResponseUtil.okWithData(categoryWithChildrensDTO);
    }
    @Override
    public ApiResponse<Object> getOneCategory(String id) {
        UUID categoryId = stringtoUUIDCategory(id);
        Category category = categoryRepository.getCategoryWithDetails(categoryId).
                orElseThrow(CategoryNotFoundException::new);
        logger.info("Category found id : {}", category.getId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
        if (authority.equals(Authority.ROLE_ADMIN)) {
           return getOneCategoryForAdmin(category);
        }
        else{
           return getOneCategoryForCustomerNoFilters(category);
        }
    }

    private void validateChildrenMatch(Category category, String newCategoryName) {
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);
        while (!queue.isEmpty()) {
            category = queue.poll();
            List<Category> childrens = categoryRepository.findByParentCategory(category);
            for (Category children : childrens) {
                if (children.getCategoryName().equalsIgnoreCase(newCategoryName)) {
                    logger.error("category name exist in one of the childrens tree");
                    throw new CategoryExistAtDepthException();
                } else {
                    queue.add(children);
                }
            }
        }
    }


    private ApiResponse<Object> updateCategoryAsRoot(Category category, String newCategoryName) {
        boolean categoryExist = categoryRepository.existsByCategoryNameAndParentCategoryIsNull(newCategoryName);
        if (categoryExist) {
            logger.error("root category already exist with same categoryName");
            throw new RootCategoryAlreadyExistException();
        }
        validateChildrenMatch(category, newCategoryName);
        category.setCategoryName(newCategoryName.toLowerCase());
        category = categoryRepository.save(category);
        return ResponseUtil.okWithData(convertToCategoryDTO(category));
    }

    private ApiResponse<Object> updateCategoryAsNonRoot(Category category, String newCategoryName) {
        validateUniqueAtBreadth(category, newCategoryName);
        validateParentsMatch(category, newCategoryName);
        validateChildrenMatch(category, newCategoryName);
        logger.info("new Category name successfully validated");
        category.setCategoryName(newCategoryName.toLowerCase());
        category = categoryRepository.save(category);
        logger.info("non-root category successfully updated");
        return ResponseUtil.okWithData(convertToCategoryDTO(category));
    }

    @Override
    public ApiResponse<Object> updateCategory(String categoryId, CategoryUpdateCO categoryUpdateCO) {
        UUID id= stringtoUUIDCategory(categoryId);
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        Category parentCategory = category.getParentCategory();
        if (parentCategory == null) {
            logger.info("updating category as root");
            return updateCategoryAsRoot(category, categoryUpdateCO.getCategoryName());
        } else {
            logger.info("updating category as non-root");
            return updateCategoryAsNonRoot(category, categoryUpdateCO.getCategoryName());
        }

    }




    private List<Category> getLeafCategories(Category category){
        List<Category> leafCategories= new ArrayList<>();
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);
        while (!queue.isEmpty()){
            Category parent = queue.poll();
            if(parent.isLeafNode()){
                leafCategories.add(parent);
            }
            else{
                List<Category> childrens = categoryRepository.findByParentCategory(parent);
                queue.addAll(childrens);
            }
        }
        logger.info("all leaf nodes successfully fetched count : {} ",leafCategories.size());
        return  leafCategories;
    }
    public  ApiResponse<Object> getCategoryInfo(String id){
        UUID categoryId = stringtoUUIDCategory(id);
        if(categoryId==null) throw new CategoryIsRequiredException();
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new InvalidCategoryIdException(categoryId));
        List<Category> categories = getLeafCategories(category);
        List<Object[]> result=  productVariationRepository.getMinAndMaxPrice(categories);
        if(result==null || result.isEmpty()){
            logger.error("no product found in given category");
            throw new NoProductFoundException();
        }


        CategoryWithFiltersDTO categoryWithFiltersDTO = CategoryWithFiltersDTO
                .builder()
                .minPrice((BigDecimal) result.get(0)[0])
                .maxPrice((BigDecimal) result.get(0)[1])
                .brands(productRepository.getAllBrandsFromCategory(categories))
                .categoryId(categoryId).categoryName(category.getCategoryName())
                .metaData(convertToFieldValueListDTO(categoryFieldValuesRepository.findByCategory(category)))
                .build();

        return ResponseUtil.okWithData(categoryWithFiltersDTO);
    }

}


