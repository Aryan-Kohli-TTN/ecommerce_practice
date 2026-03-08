package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.co.ProductSaveCO;
import com.bootcamp.co.ProductUpdateCO;
import com.bootcamp.co.ProductVariationSaveCO;
import com.bootcamp.co.ProductVariationUpdateCO;
import com.bootcamp.dto.*;
import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.category.CategoryField;
import com.bootcamp.entity.category.CategoryFieldValues;
import com.bootcamp.entity.product.Product;
import com.bootcamp.entity.product.ProductVariation;
import com.bootcamp.enums.Authority;
import com.bootcamp.entity.user.Seller;
import com.bootcamp.exception.category.CategoryIsNonLeafException;
import com.bootcamp.exception.category.InvalidCategoryIdException;
import com.bootcamp.exception.invalidFormat.InvalidMaxPriceException;
import com.bootcamp.exception.invalidFormat.InvalidMinPriceException;
import com.bootcamp.exception.invalidFormat.InvalidUUIDException;
import com.bootcamp.exception.product.*;
import com.bootcamp.repository.category.CategoryFieldRepository;
import com.bootcamp.repository.category.CategoryRepository;
import com.bootcamp.repository.product.CustomRepository;
import com.bootcamp.repository.product.ProductRepository;
import com.bootcamp.repository.product.ProductVariationRepository;
import com.bootcamp.repository.user.SellerRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.*;
import com.bootcamp.utils.MyBeanUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final MessageSource messageSource;
    private final EmailService emailService;
    private final ImageValidationService imageValidationService;
    private final ImageService imageService;
    private final CategoryFieldRepository categoryFieldRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ParamsValidationsService paramsValidationsService;
    private final CustomRepository customRepository;



    private Product COtoProduct(ProductSaveCO productSaveCO, Seller seller, Category category) {
        logger.info("product converted to co");
        return Product.builder().productName(productSaveCO.getProductName())
                .productDescription(productSaveCO.getProductDescription())
                .productBrand(productSaveCO.getProductBrand())
                .isCancellable(productSaveCO.isCancellable())
                .isReturnable(productSaveCO.isReturnable())
                .isActive(false).isDeleted(false).seller(seller)
                .category(category).auditing(new Auditing()).build();
    }

    void validateProductExist(String productName, Category category, String ProductBrand, Seller seller) {
        boolean productExists = productRepository.existsByProductNameAndCategoryAndProductBrandAndSeller(
                productName,
                category, ProductBrand, seller);
        if (productExists) {
            logger.error("Product already exist so {} {}",productName,category.getCategoryName());
            throw new ProductAlreadyExistException();
        }
    }

    private Seller getSellerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return sellerRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(""));
    }

    @Override
    public ApiResponse<Object> saveProduct(ProductSaveCO productSaveCO) {
        Seller seller = getSellerFromAuthentication();
        Category category = categoryRepository.findById(productSaveCO.getCategoryId())
                .orElseThrow(() -> new InvalidCategoryIdException(productSaveCO.getCategoryId()));
        if (!category.isLeafNode()) {
            logger.error("category is not leaf category");
            throw new CategoryIsNonLeafException();
        }
        validateProductExist(productSaveCO.getProductName(), category, productSaveCO.getProductBrand(), seller);
        Product product = COtoProduct(productSaveCO, seller, category);
        product = productRepository.save(product);

        seller.addProduct(product);
        sellerRepository.save(seller);

        category.addProduct(product);
        categoryRepository.save(category);

        emailService.sendActivateProductMail(product);
        Locale locale = LocaleContextHolder.getLocale();
        logger.info("Product Saved successfully {} ",product.getId());
        return ResponseUtil.ok(messageSource.getMessage("message.product.saved", new Object[]{}, locale));
    }

    private Product validateAndGetProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        logger.info("Product info fetched");
        if (!product.isActive()){
            logger.error("Product not active {}",id);
            throw new ProductNotActiveException();
        }
        return product;
    }

    private void validateSellerAndProduct(Seller seller, Product product) {
        if (!product.getSeller().getId().equals(seller.getId())) {
            logger.error("Product not assosiated with seller sellerID:{} productId: {}",seller.getId(),product.getId());
            throw new ProductNotAssociatedWithSellerException();
        }
    }

    private void validateMetaData(TreeMap<String, String> metaData) {
        if (metaData.isEmpty()) {
            logger.error("Provided meta data is empty");
            throw new ProductMetadataMinCountException();
        }

    }

    private void validatePrimaryImage(MultipartFile primaryImage) {
        imageValidationService.validateImageFile(primaryImage, "Primary Image");
        logger.info("primary image successfully validated");
    }

    private void validateSecondaryImages(MultipartFile[] secondaryImages) {
        int index = 0;
        for (MultipartFile secondaryImage : secondaryImages) {
            imageValidationService.validateImageFile(secondaryImage, "Secondary Image [" + index + "]");
            logger.info("Secondary image at index {} successfully validated",index);
            index++;
        }

    }

    private List<CategoryField> getAllCategoryFields(Set<String> fields) {
        logger.info("finding the category fields from field names");
        return categoryFieldRepository.findByCategoryFieldNameIn(fields);
    }

    private Map<String, String> getLowerCaseMap(TreeMap<String, String> upperCaseMetadata) {
        logger.info("getting the lower case hashMap");
        return upperCaseMetadata.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toLowerCase(),
                        entry -> entry.getValue().toLowerCase(),
                        (v1, v2) -> v1,
                        TreeMap::new
                ));
    }

    private void validateFieldValues(Category category, TreeMap<String, String> upperCaseMetadata) {
        TreeMap<String, String> metaData = (TreeMap<String, String>) getLowerCaseMap(upperCaseMetadata);

        logger.info("In validate field values");
        List<CategoryField> categoryFieldList = getAllCategoryFields(metaData.keySet()); // given by user
        if (categoryFieldList.size() != metaData.size()) {
            logger.error("Some fields does not exist");
            throw new InvalidFieldsException();
        }
        logger.info("all  fields provided exist in database");
        Set<CategoryFieldValues> categoryFieldValuesSet = category.getCategoryFieldValuesList();

        HashMap<UUID, List<String>> fieldIdWithValues = new HashMap<>();
        categoryFieldValuesSet.forEach((categoryFieldValues -> {
            fieldIdWithValues.put(categoryFieldValues.getCategoryField().getId(),
                    Arrays.asList(categoryFieldValues.getFieldValues().split(",")));
        }));
        logger.info("fieldIds with possible values stored in hashMap");
        categoryFieldList.forEach((categoryField -> {
            List<String> possibleValues = fieldIdWithValues.getOrDefault(categoryField.getId(), null);
            // reterving the poosible values for fields like {"xl","xxl","l"} etc
            String field = categoryField.getCategoryFieldName();
            if (possibleValues == null) {
                logger.error("field {} does not exist", field);
                throw new FieldNotAssociatedException(field);
            }
            // reteriving the value of field given by user
            String value = metaData.get(field);

            // checking if provided value exist in db
            if (!possibleValues.contains(value)) {
                logger.error("field {}  haves invalid values", field);
                throw new InvalidFieldValuesException(field);
            }
        }));
        logger.info("All metadata fields-values successfully validated");
        //fieldsId and value that exist in category
    }

    private void validatePreviousExistData(TreeMap<String, String> upperCaseData, Product product) {
        TreeMap<String, String> metaData = (TreeMap<String, String>) getLowerCaseMap(upperCaseData);
        List<ProductVariation> productVariationList = productVariationRepository.findByProduct(product);
        // here reterving all variations of product is wrong
        // we can also simply write query using JSON_EXTRACT and verify
        // like i did in extract all products with metadata etc.
        logger.info("Reterived all variations of product to check same existence");
        if (!productVariationList.isEmpty()) {
            TreeMap<String, String> prevData = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                prevData = objectMapper.readValue(productVariationList.get(0).getMetaData(), new TypeReference<TreeMap<String, String>>() {
                });
            } catch (JsonProcessingException e) {
                throw new com.bootcamp.exception.invalidFormat.JsonProcessingException();
            }
            if (prevData != null && !prevData.keySet().equals(metaData.keySet())) {
                logger.error("Product Variation Structure does not match for product {} ", product.getId());
                throw new VariationStructureNotMatchException();
            }
            logger.info("Product Variation Structure is validated, its structure similar to previous one.");
            productVariationList.forEach(productVariation -> {

                TreeMap<String, String> prevMetaData;
                try {
                    prevMetaData = objectMapper.readValue(productVariation.getMetaData(), new TypeReference<TreeMap<String, String>>() {
                    });
                } catch (JsonProcessingException e) {
                    throw new com.bootcamp.exception.invalidFormat.JsonProcessingException();
                }
                if (metaData.equals(prevMetaData)) {
                    logger.error("Product variation already exist for product : {}", product.getId());
                    throw new ProductVariationAlreadyExistException();
                }
            });
            logger.info("No Previous varuiation is exactly same");
        }
    }

    private ProductVariation convertToEntity(ProductVariationSaveCO productVariationSaveCO, Product product) {
        String jacksonData;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            jacksonData = objectMapper.writeValueAsString(getLowerCaseMap(productVariationSaveCO.getMetaData()));
        } catch (Exception e) {
            throw new com.bootcamp.exception.invalidFormat.JsonProcessingException();
        }
        return ProductVariation.builder()
                .productVariationQuantity(productVariationSaveCO.getQuantity())
                .productVariationPrice(productVariationSaveCO.getPrice())
                .isActive(true).isDeleted(false).product(product).auditing(new Auditing())
                .metaData(jacksonData).build();
    }

    @Override
//    @Transactional
    public ApiResponse<Object> saveProductVariation(ProductVariationSaveCO productVariationSaveCO) {
        validateMetaData(productVariationSaveCO.getMetaData());
        validatePrimaryImage(productVariationSaveCO.getPrimaryImage());
        validateSecondaryImages(productVariationSaveCO.getSecondaryImage());
        Product product = validateAndGetProduct(productVariationSaveCO.getProductId());
        Seller seller = getSellerFromAuthentication();
        Category category = product.getCategory();
        validateSellerAndProduct(seller, product);
        validateFieldValues(category, productVariationSaveCO.getMetaData());
        validatePreviousExistData(productVariationSaveCO.getMetaData(), product);
        logger.info("All input validations are done for product variations");


        ProductVariation productVariation = convertToEntity(productVariationSaveCO, product);
        productVariation = productVariationRepository.save(productVariation);
        if (productVariationSaveCO.getSecondaryImage().length > 10) {
            throw new SecondaryImageMaxException();
        }
        imageService.saveProductPrimaryImage(productVariationSaveCO.getPrimaryImage(), productVariation.getId());
        imageService.saveProductSecondaryImages(productVariationSaveCO.getSecondaryImage(), productVariation.getId());
        logger.info("Images uploaded successfully");
        product.addProductVariation(productVariation);
        productRepository.save(product);
        logger.info("Product variation added successfully");
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.variation.added.success",null,locale));
    }


    private ProductDTO convertToDto(Product product) {
        return ProductDTO.builder().productId(product.getId())
                .productName(product.getProductName())
                .productBrand(product.getProductBrand())
                .productDescription(product.getProductDescription())
                .isCancellable(product.isCancellable())
                .isReturnable(product.isReturnable())
                .category(new CategoryDTO(product.getCategory().getId(), product.getCategory().getCategoryName()
                        , product.getCategory().getParentCategory().getId()))
                .build();
    }
    private StringBuilder getProductQuery(Map<String,String> metadata,  List<String> categoryIds, String sellerId,
                                     String productBrand,String productName,String orderBy,String sortBy
            ,int pageSize,int pageOffset,BigDecimal minPrice,BigDecimal maxPrice,boolean isActive){
        StringBuilder query = new StringBuilder("Select p.product_id, p.product_name,p.product_description,p.product_brand,p.is_cancellable," +
                "p.is_returnable,pv.product_variation_id, pv.product_variation_price,pv.product_variation_quantity," +
                "pv.meta_data"+", c.category_id, "+" c.category_name,"+" c.parent_category_id "+
                " from product p LEFT JOIN product_variation pv on p.product_id = pv.product_id LEFT JOIN category c on" +
                " p.category_id=c.category_id WHERE p.is_deleted=false ");
        if (categoryIds != null && !categoryIds.isEmpty()) {
            query.append("AND p.category_id IN ( ");
            int size = categoryIds.size();
            for(int i=0;i<size;i++){
                if(i!=size-1)
                query.append("UUID_TO_BIN('").append(categoryIds.get(i)).append("') , ");
                else{
                    query.append("UUID_TO_BIN('").append(categoryIds.get(i)).append("') ) ");
                }
            }
        }
        if(minPrice!=null){
            query.append("AND pv.product_variation_price >= ").append(minPrice.toString());
        }
        if(maxPrice!=null){
            query.append("AND pv.product_variation_price <= ").append(maxPrice.toString());
        }
        // must for admin and customer
        if(isActive){
            query.append("AND p.is_active = true AND pv.is_active IS NOT NULL AND pv.is_active=true ");
        }
        if (sellerId != null) {
            query.append("AND p.seller_user_id = UUID_TO_BIN('").append(sellerId).append("') ");
        }
        if (productBrand != null) {
            query.append("AND p.product_brand LIKE '%").append(productBrand.replace("'", "''")).append("%' ");
        }
        if (productName != null) {
            query.append("AND p.product_name LIKE '%").append(productName.replace("'", "''")).append("%' ");
        }
        for(String field: metadata.keySet()){
            String value = metadata.get(field).replace("'","''");
            query.append("AND JSON_EXTRACT(pv.meta_data, '$.\"").append(field).append("\"') LIKE '%").append(value).append("%' ");
        }
        query.append(" ORDER BY ").append(sortBy).append(" ").append(orderBy).append(" LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset);
        query.append(";");
        logger.info("Get product query made");
        return query;
    }
    private UUID convertId(Object id ){
        byte[] bytes = (byte[]) id;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
    private ProductVariationDTO objectArrayToProductVariationDTO(Object[] product){
        UUID productVariationId = convertId(product[6]);
        logger.info("productVar id : {}",productVariationId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> secondaryImages = new ArrayList<>();
        String primaryImage = null;
        boolean exists = imageService.productVarPrimaryImageExist(productVariationId);
        if (exists) {
            primaryImage = "http://localhost:8080/api/image/product-variation/primary/" +productVariationId.toString();
        }
        for (int i = 1; i <= 10; i++) {
            boolean secondaryImageexists = imageService.productVarSecondaryImageExistsByIndex(productVariationId, i);
            if (secondaryImageexists) {
                secondaryImages.add("http://localhost:8080/api/image/product-variation/secondary/"
                        + productVariationId + "/" + i);
            } else {
                break;
            }
        }
        ProductVariationDTO productVariationDTO;
        try {
            productVariationDTO= ProductVariationDTO.builder()
                    .id(productVariationId)
                    .productVariationQuantity(((BigDecimal) product[8]).toBigInteger())
                    .metaData(objectMapper.readTree((String) product[9]))
                    .productVariationPrice((BigDecimal) product[7])
                    .secondaryImages(secondaryImages).primaryImage(primaryImage)
                    .build();
        }
        catch (Exception e){
            throw new com.bootcamp.exception.invalidFormat.JsonProcessingException();
        }
        logger.info("Object [] to productVariationDTO successfully made");
        return productVariationDTO;
    }

    private void addInitialProductInMap(Map<UUID,ProductVariationListDTO> productVariationListDTOMap,Object[] product,UUID productId){
        // map i storing product ---> all variations
        // so when got first variation add entry in map
        UUID catId =convertId(product[10]);
        UUID parentCategoryId = convertId(product[12]);
        CategoryDTO categoryDTO=
                CategoryDTO.builder().categoryName((String) product[11]).id(catId).parentCategoryId(parentCategoryId).build();

        ProductDTO productDTO = ProductDTO.builder()
                .productId(productId)
                .productName((String) product[1])
                .productDescription((String) product[2])
                .productBrand((String) product[3])
                .isCancellable((boolean) product[4])
                .isReturnable((boolean) product[5])
                .category(categoryDTO)
                .build();

        productVariationListDTOMap.put(productId,
                ProductVariationListDTO.builder().productInfo(productDTO).variations(new ArrayList<>()).build());
        logger.info("First Entry for product is done in ProductVariationListDTO");
    }
    private BigDecimal getMinPrice(String minPrice){

        try{
            logger.info("converting min price to BigDecimal");
            if(minPrice!=null)
            {
                return new BigDecimal(minPrice);
            }
        }
        catch (NumberFormatException e){
            throw new InvalidMinPriceException();
        }
        return null;
    }
    private BigDecimal getMaxPrice(String maxPrice){
        try{
            logger.info("converting max price to BigDecimal");
            if(maxPrice!=null)
                return new BigDecimal(maxPrice);
        }
        catch (NumberFormatException e){
            throw new InvalidMaxPriceException();
        }
        return null;
    }
    private List<ProductVariationListDTO> getProducts(Map<String,String> metadata,  List<String> categoryId, String sellerId,
                                                     String productBrand,String productName,String orderBy,String sortBy
            ,int pageSize,int pageOffset,String minPrice,String maxPrice ,boolean isActive)  {

        BigDecimal minPriceDecimal=getMinPrice(minPrice),maxPriceDecimal=getMaxPrice(maxPrice);
        StringBuilder query = getProductQuery(metadata,categoryId,sellerId,productBrand,productName,orderBy,sortBy,pageSize,pageOffset,minPriceDecimal,maxPriceDecimal,isActive);
        logger.info("GetProduct Query successfully reterived");
        List<Object[]>result  = customRepository.executeQuery(String.valueOf(query));
        logger.info("GetProduct Query successfully executed");
        Map<UUID,ProductVariationListDTO> productVariationListDTOMap = new HashMap<>();
        result.forEach(product->{
            UUID productId  =convertId(product[0]);
            if(!productVariationListDTOMap.containsKey(productId)){
                addInitialProductInMap(productVariationListDTOMap,product,productId);
            }
            ProductVariationListDTO productVariationListDTO = productVariationListDTOMap.get(productId);
            if(product[6]!=null){
                productVariationListDTO.addProductVariationDto(objectArrayToProductVariationDTO(product));
            }
        });
        logger.info("productVariationListDTOMap made ");
        try{
            Collection<ProductVariationListDTO> values = productVariationListDTOMap.values();
            return new ArrayList<>(values);
        } catch (Exception e) {
            logger.warn("Error while converting productVariationListDTOMap list ofProductVariationListDTO ");
            return new ArrayList<>();
        }
    }


    @Override
    public ApiResponse<Object> getAllProducts(Map<String,String> allParams){
        int pageSize = paramsValidationsService.getPageSize(allParams.getOrDefault("pageSize","10"));
        int pageOffset = paramsValidationsService.getPageOffset(allParams.getOrDefault("pageOffset","0"));
        String orderBy = paramsValidationsService.getOrderByString(allParams.getOrDefault("orderBy","asc"));
        String sortBy = paramsValidationsService.getSortByForProduct(allParams.getOrDefault("sortBy","id"));
        String categoryId = allParams.getOrDefault("categoryId",null);
        String sellerId = allParams.getOrDefault("sellerId",null);
        String productName = allParams.getOrDefault("productName", null);
        String productBrand = allParams.getOrDefault("productBrand", null);
        String maxPrice = allParams.getOrDefault("maxPrice", null);
        String minPrice = allParams.getOrDefault("minPrice", null);
        productName = productName != null ? productName.toLowerCase() : null;
        productBrand = productBrand != null ? productBrand.toLowerCase() : null;

        Set<String> knownParams = Set.of(
                "pageSize", "pageOffset", "sortBy", "productName", "productBrand", "orderBy", "sellerId", "categoryId",
                "minPrice","maxPrice"
        );

        Map<String, String> filters = allParams.entrySet().stream()
                .filter(entry -> !knownParams.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toLowerCase(),
                        entry -> entry.getValue().toLowerCase()
                ));

        logger.info("All params validated");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();

        if(authority.equals(Authority.ROLE_CUSTOMER) && categoryId==null){
            logger.error("CategoryId is required for customer to fetch data");
            throw new CategoryIsRequiredException();
        }
        List<Category> leafCategories = getLeafCategories(categoryId);
        if (authority.equals(Authority.ROLE_ADMIN)) {
            // can se all active and non deleted products
            logger.info("In get All products for Admin");
            return getAllProductsWithVariations( leafCategories,filters, sellerId,
                     productBrand,productName,orderBy,sortBy
                    ,pageSize,pageOffset,minPrice,maxPrice ,true);
        }
        else if(authority.equals(Authority.ROLE_SELLER)){
            Seller seller = getSellerFromAuthentication();
            logger.info("In get All products for Seller");
            logger.info("Seller can view non active product details also");
            return getAllProductsWithVariations( leafCategories,filters, seller.getId().toString(),
                    productBrand,productName,orderBy,sortBy
                    ,pageSize,pageOffset,minPrice,maxPrice ,false);
        }
        else{
            logger.info("In get All products for customer");
            return getAllProductsWithVariations( leafCategories,filters, sellerId,
                    productBrand,productName,orderBy,sortBy
                    ,pageSize,pageOffset,minPrice,maxPrice ,true);
        }
    }

    private ApiResponse<Object> getOneProductSeller(Seller seller, UUID productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException();
        }
        validateSellerAndProduct(seller,productOptional.get());
        logger.info("GetOneProductSeller successfully reterived basic info no variation");
        return ResponseUtil.okWithData(convertToDto(productOptional.get()));
    }


    private ApiResponse<Object> getOneProductCustomer(UUID productId){
        // customer views only non-deleted and active products
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            logger.error("Product Not Found");
            throw new ProductNotFoundException();
        }
        if(!productOptional.get().isActive()){
            logger.error("Product is inActive");
            throw new ProductInActiveException();
        }
        if(productOptional.get().getProductVariationList().isEmpty()){
            logger.error("No ProductVariation is present");
            throw new NoProductVariationException();
        }
        logger.info("GetOneProductCustomer successfully reterived basic info no variation");
        return ResponseUtil.okWithData(convertToProductWithImageDto(productOptional.get()));
    }
    private ApiResponse<Object> getOneProductAdmin(UUID productId){
        // for admin we are getting also deleted and inactiveproducts
        Optional<Product> productOptional = productRepository.getProductForAdmin(productId);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException();
        }
        logger.info("GetOneProductAdmin successfully reterived basic info no variation");
        return ResponseUtil.okWithData(convertToDto(productOptional.get()));
    }

    @Override
    public ApiResponse<Object> getOneProduct(String id) {
        UUID productId = stringToUUIDProduct(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
        logger.info("inside get one product basic info");
        if (authority.equals(Authority.ROLE_ADMIN)) {
            return getOneProductAdmin(productId);
        } else if (authority.equals(Authority.ROLE_CUSTOMER)) {
            return getOneProductCustomer(productId);
        } else {
            Seller seller = sellerRepository.findByEmail(authentication.getName()).get();
            return getOneProductSeller(seller, productId);
        }
    }


    private ProductVariationDTO convertToProductVariationDTO(ProductVariation productVariation) {
        List<String> secondaryImages = new ArrayList<>();
        String primaryImage = null;
        boolean exists = imageService.productVarPrimaryImageExist(productVariation.getId());
        if (exists) {
            primaryImage = "http://localhost:8080/api/image/product-variation/primary/" + productVariation.getId().toString();
        }
        for (int i = 1; i <= 10; i++) {
            boolean secondaryImageexists = imageService.productVarSecondaryImageExistsByIndex(productVariation.getId(), i);
            if (secondaryImageexists) {
                secondaryImages.add("http://localhost:8080/api/image/product-variation/secondary/"
                        + productVariation.getId() + "/" + i);
            } else {
                break;
            }
        }
        logger.info("Converting Product Variation to ProductVariationDTO");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return ProductVariationDTO.builder()
                    .productVariationQuantity(productVariation.getProductVariationQuantity())
                    .id(productVariation.getId()).productVariationPrice(productVariation.getProductVariationPrice())
                    .primaryImage(primaryImage).secondaryImages(secondaryImages)
                    .metaData(objectMapper.readTree(productVariation.getMetaData())).build();
        } catch (Exception e) {
            logger.error("json processing error while convert product variation to productVariationDTO");
            throw new com.bootcamp.exception.invalidFormat.JsonProcessingException();
        }
    }

    private StringBuilder getProductVariationQuery(Map<String,String> metadata,  String productId, String sellerId,
                                          String orderBy,String sortBy
            ,int pageSize,int pageOffset,BigDecimal minPrice,BigDecimal maxPrice){
        StringBuilder query = new StringBuilder("Select p.product_id, p.product_name,p.product_description,p.product_brand,p.is_cancellable," +
                "p.is_returnable,pv.product_variation_id, pv.product_variation_price,pv.product_variation_quantity," +
                "pv.meta_data"+", c.category_id, "+" c.category_name,"+" c.parent_category_id "+
                " from product p LEFT JOIN product_variation pv on p.product_id = pv.product_id LEFT JOIN category c on" +
                " p.category_id=c.category_id WHERE p.is_deleted=false AND p.product_id = UUID_TO_BIN('").append(productId).append("')");
        if(minPrice!=null){
            query.append("AND pv.product_variation_price >= ").append(minPrice.toString());
        }
        if(maxPrice!=null){
            query.append("AND pv.product_variation_price <= ").append(maxPrice.toString());
        }
        if (sellerId != null) {
            query.append("AND p.seller_user_id = UUID_TO_BIN('").append(sellerId).append("') ");
        }
        for(String field: metadata.keySet()){
            String value = metadata.get(field).replace("'","''");
            query.append("AND JSON_EXTRACT(pv.meta_data, '$.").append(field).append("') LIKE '%").append(value).append("%' ");
        }
        query.append(" ORDER BY ").append(sortBy).append(" ").append(orderBy).append(" LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset);
        query.append(";");
        logger.info("Get product variation Query has been made.");
        return query;
    }


    private List<ProductVariationListDTO> getProductVariations(Map<String,String> metadata,  String productId, String sellerId,
                                                      String orderBy,String sortBy
            ,int pageSize,int pageOffset,String minPrice,String maxPrice )  {

        BigDecimal minPriceDecimal=getMinPrice(minPrice);
        BigDecimal maxPriceDecimal = getMaxPrice(maxPrice);
        StringBuilder query = getProductVariationQuery(metadata,productId,sellerId,orderBy,sortBy,pageSize,pageOffset,minPriceDecimal,maxPriceDecimal);
        List<Object[]>result  = customRepository.executeQuery(String.valueOf(query));
        Map<UUID,ProductVariationListDTO> productVariationListDTOMap = new HashMap<>();
        result.forEach(product->{
            UUID productIdUUID  =convertId(product[0]);
            if(!productVariationListDTOMap.containsKey(productIdUUID)){
                addInitialProductInMap(productVariationListDTOMap,product,productIdUUID);
            }
            ProductVariationListDTO productVariationListDTO = productVariationListDTOMap.get(productIdUUID);
            if(product[6]!=null){
                productVariationListDTO.addProductVariationDto(objectArrayToProductVariationDTO(product));
            }
        });
        try{
            Collection<ProductVariationListDTO> values = productVariationListDTOMap.values();
            return new ArrayList<>(values);


        } catch (Exception e) {
            logger.error("error in converting the productVariationListDto to list of values");
            return new ArrayList<>();
        }
    }

    private void validateSellerAndProductId(UUID sellerId, UUID productId){
        Optional<Seller> sellerOptional = productRepository.getSellerOfProduct(productId);
        logger.info("validating product is associated with seller or not ");
        if(sellerOptional.isEmpty()){
            logger.error("Product not found");
            throw new ProductNotFoundException();
        }
        if(!sellerId.equals(sellerOptional.get().getId())){
            logger.error("Product not associated with seller");
            throw new ProductNotAssociatedWithSellerException();
        }
    }
    @Override
    public ApiResponse<Object> getAllProductVariation(Map<String,String> allParams) {
        int pageSize = paramsValidationsService.getPageSize(allParams.getOrDefault("pageSize","10"));
        int pageOffset = paramsValidationsService.getPageOffset(allParams.getOrDefault("pageOffset","0"));
        String orderBy = paramsValidationsService.getOrderByString(allParams.getOrDefault("orderBy","asc"));
        String sortBy = paramsValidationsService.getSortByForProduct(allParams.getOrDefault("sortBy","id"));
        String productId = allParams.getOrDefault("productId",null);
        String maxPrice = allParams.getOrDefault("maxPrice",null);
        String minPrice = allParams.getOrDefault("minPrice",null);

        if(productId==null){
            throw new ProductNotFoundException();
        }
        Set<String> knownParams = Set.of(
                "pageSize", "pageOffset", "sortBy", "productName", "productBrand", "orderBy", "sellerId", "categoryId",
                "minPrice","maxPrice","productId"
        );

        Map<String, String> filters = allParams.entrySet().stream()
                .filter(entry -> !knownParams.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toLowerCase(),
                        entry -> entry.getValue().toLowerCase()
                ));

        logger.info("All params validated in get all product variation");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
        if (authority.equals(Authority.ROLE_SELLER)) {
            Seller seller = getSellerFromAuthentication();
            UUID productIdUUID = stringToUUIDProduct(productId);
            validateSellerAndProductId(seller.getId(), productIdUUID);
            logger.info("getting all product variations for seller");
            //for seller he can access only his product
            return ResponseUtil.okWithData(getProductVariations(filters,productId,seller.getId().toString(),orderBy,sortBy,pageSize,
                    pageOffset,minPrice,maxPrice));
        }
        else{
            logger.info("getting all product variation for admin or customer");
            //for customer/admin he can access any product
            return ResponseUtil.okWithData(getProductVariations(filters,productId,null,orderBy,sortBy,pageSize,
                    pageOffset,minPrice,maxPrice));
        }
    }

    @Override
    public ApiResponse<Object> getOneProductVariation(String productvariationId) {
        UUID id = stringToUUIDProductVariation(productvariationId);
        ProductVariation productVariation = productVariationRepository.findById(id)
                .orElseThrow(ProductVariationNotFoundException::new);
        Product product = productVariation.getProduct();
        Seller seller = getSellerFromAuthentication();
        validateSellerAndProduct(seller, product);
        logger.info("getting details about specific product variation : {}",id);
        ProductVariationWithProductDTO productVariationWithProductDTO = ProductVariationWithProductDTO
                .builder().productVariation(convertToProductVariationDTO(productVariation))
                .product(convertToDto(product)).build();
        return ResponseUtil.okWithData(productVariationWithProductDTO);
    }

    @Override
    public ApiResponse<Object> updateProduct(String id, ProductUpdateCO productUpdateCO) {
        UUID productId = stringToUUIDProduct(id);
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Seller seller = getSellerFromAuthentication();
        validateSellerAndProduct(seller, product);
        logger.info("updating the product details product id : {}",productId);
        BeanUtils.copyProperties(productUpdateCO, product, MyBeanUtils.getNullPropertyNames(productUpdateCO));
        validateProductExist(productUpdateCO.getProductName(), product.getCategory(), product.getProductBrand(), seller);
        productRepository.save(product);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.updated.success",null,locale));
    }


    private void validateVariationUpdateRequest(ProductVariationUpdateCO productVariationUpdateCO, ProductVariation productVariation) {

        Product product = productVariation.getProduct();
        Seller seller = getSellerFromAuthentication();
        Category category = product.getCategory();
        validateSellerAndProduct(seller, product);
        logger.info("Product and seller are validated");
        if (!(productVariationUpdateCO.getMetaData() == null || productVariationUpdateCO.getMetaData().isEmpty())) {
            validateMetaData(productVariationUpdateCO.getMetaData());
            validateFieldValues(category, productVariationUpdateCO.getMetaData());
            validatePreviousExistData(productVariationUpdateCO.getMetaData(), product);
            logger.info("product variation updating metadata is verified");
        }
        if (productVariationUpdateCO.getPrimaryImage() != null) {
            validatePrimaryImage(productVariationUpdateCO.getPrimaryImage());
            logger.info("product variation updating primaryImage is verified");
        }
        if (!(productVariationUpdateCO.getSecondaryImage() == null
                || productVariationUpdateCO.getSecondaryImage().length == 0)) {
            validateSecondaryImages(productVariationUpdateCO.getSecondaryImage());
            logger.info("product variation updating secondaryImage is verified");

        }
    }

    private void saveUpdatedImages(ProductVariationUpdateCO productVariationUpdateCO, ProductVariation productVariation) {
        if (productVariationUpdateCO.getPrimaryImage() != null) {
            imageService.saveProductPrimaryImage(productVariationUpdateCO.getPrimaryImage(), productVariation.getId());
            logger.info("product image successfully saved");
        }
        if (!(productVariationUpdateCO.getSecondaryImage() == null
                || productVariationUpdateCO.getSecondaryImage().length == 0)) {
            logger.info("product secondary image successfully saved");
            imageService.saveProductSecondaryImages(productVariationUpdateCO.getSecondaryImage(), productVariation.getId());
        }
    }

    @Override
    public ApiResponse<Object> updateProductVariation(String id,
                                                      ProductVariationUpdateCO productVariationUpdateCO) {
        UUID productVariationId = stringToUUIDProductVariation(id);
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(ProductVariationNotFoundException::new);
        validateVariationUpdateRequest(productVariationUpdateCO, productVariation);
        logger.info("All input validations are done for product variations update request");
        saveUpdatedImages(productVariationUpdateCO, productVariation);

        BeanUtils.copyProperties(productVariationUpdateCO, productVariation, MyBeanUtils.
                getNullPropertyNames(productVariationUpdateCO));
        productVariationRepository.save(productVariation);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.variation.updated.success",null,locale));
    }

    private void deleteAllVariations(Product product){
        productVariationRepository.findByProduct(product).forEach(productVariationRepository::delete);
        logger.info("All product variations deleted successfully");
    }

    private UUID stringToUUIDProduct(String id){
        UUID productId =null;
        try{
            productId= UUID.fromString(id);
        }
        catch (Exception e){
            throw new InvalidProductIdException();
        }
        return productId;
    }
    private UUID stringToUUIDProductVariation(String id){
        UUID productVariationId =null;
        try{
            productVariationId= UUID.fromString(id);
        }
        catch (Exception e){
            throw new InvalidProductVariationIdException();
        }
        return productVariationId;
    }
    @Override
    public ApiResponse<Object> deleteProduct(String id){
        UUID productId = stringToUUIDProduct(id);
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Seller seller = getSellerFromAuthentication();
        validateSellerAndProduct(seller,product);
        deleteAllVariations(product);
        productRepository.deleteById(productId);
        logger.info("All product & its variations are deleted");
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.deleted.successfully",null,locale));
    }

    @Override
    public ApiResponse<Object> activateProduct(String id){
        UUID productId = stringToUUIDProduct(id);
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        if(product.isActive())
        {
            logger.error("Product already activated {}",productId);
            throw new ProductAlreadyActivated();
        }
        product.setActive(true);
        productRepository.save(product);
        emailService.productActivatedMail(product.getSeller().getEmail(),product);
        logger.info("product successfully activated {}",productId);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.activated",null,locale));
    }
    @Override
    public ApiResponse<Object> deactivateProduct(String id){
        UUID productId = stringToUUIDProduct(id);
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        if(!product.isActive())
        {
            logger.error("Product already deactivated {}",productId);
            throw new ProductAlreadyDeactivated();
        }
        product.setActive(false);
        productRepository.save(product);
        emailService.productDeactivatedMail(product.getSeller().getEmail(),product);
        logger.info("product successfully deactivated {}",productId);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.product.deactivated",null,locale));
    }
    private ProductWithImagesDTO convertToProductWithImageDto(Product product)
    {
        List<Object[]> variationsId = productVariationRepository.getAllIdOfVariations(product);
        List<String> imageLinks = variationsId.stream().map((varition)->{
            return
            "http://localhost:8080/api/image/product-variation/primary/"+varition[0].toString();
        }).toList();
        logger.info("converting the product to product with image DTO");
        return ProductWithImagesDTO.builder().productId(product.getId())
                .productName(product.getProductName())
                .productBrand(product.getProductBrand())
                .productDescription(product.getProductDescription())
                .isCancellable(product.isCancellable())
                .isReturnable(product.isReturnable())
                .category(new CategoryDTO(product.getCategory().getId(), product.getCategory().getCategoryName()
                        , product.getCategory().getParentCategory().getId()))
                .images(imageLinks)
                .build();
    }
    private ApiResponse<Object> getAllProductsWithVariations
            (List<Category> categoryList,Map<String,String> metadata, String sellerId,
             String productBrand,String productName,String orderBy,String sortBy
                    ,int pageSize,int pageOffset,String minPrice,String maxPrice ,boolean isActive){
        List<String> categoryIds = categoryList==null?null:categoryList.stream().map(category -> category.getId().toString()).toList();
        logger.info("getting all products with variation");
        return ResponseUtil.okWithData(getProducts(metadata,categoryIds,sellerId,productBrand,productName,orderBy,sortBy
        ,pageSize,pageOffset,minPrice,maxPrice ,isActive));
    }

    private List<Category> getLeafCategories(String categoryId){
        if(categoryId==null)
            return null;
        UUID catId;
        try{
            catId= UUID.fromString(categoryId);
        } catch (Exception e) {
            logger.error("invalid uuid format for categoryId");
            throw new InvalidUUIDException();
        }
        Category category = categoryRepository.findById(catId).orElseThrow(()->new InvalidCategoryIdException(catId));
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
        logger.info("All leaf categories succesfully fetched count : {} ",leafCategories.size());
        return  leafCategories;
    }


    @Override
    public ApiResponse<Object> getSimilarProduct(String id,String Size, String Offset, String orderBy, String sortBy){
        UUID productId = stringToUUIDProduct(id);
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        int pageSize = paramsValidationsService.getPageSize(Size);
        int pageOffset = paramsValidationsService.getPageOffset(Offset);
        sortBy = paramsValidationsService.getSortBy(sortBy, Arrays.asList("id",
                "createdAt", "updatedAt", "auditing.createdAt", "auditing.updatedAt"));

        Sort.Direction direction = paramsValidationsService.getOrderBy(orderBy);
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(direction, sortBy));
        List<Product> products = productRepository.findByProductBrandAndIdNot(product.getProductBrand(),productId,pageable);
        logger.info("getting similar products , current finding with same brand");
        return ResponseUtil.okWithData(products.stream().map(this::convertToProductWithImageDto).toList());
    }
}
