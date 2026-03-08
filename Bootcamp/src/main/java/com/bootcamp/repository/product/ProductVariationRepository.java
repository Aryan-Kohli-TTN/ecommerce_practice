package com.bootcamp.repository.product;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.product.Product;
import com.bootcamp.entity.product.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {
    boolean existsByMetaDataAndProduct(String metaData, Product product);
    List<ProductVariation> findByProduct(Product product);
    List<ProductVariation> findByProduct(Product product, Pageable pageable);
    void deleteAllByProduct(Product product);

    @Query("Select p.id from ProductVariation p where p.product=:product")
    List<Object[]> getAllIdOfVariations(@Param("product") Product product);

    @Query("Select MIN(pv.productVariationPrice),MAX(pv.productVariationPrice) from ProductVariation pv LEFT JOIN pv.product p  " +
            " where p.category IN :categoryList")
    List<Object[]> getMinAndMaxPrice(@Param("categoryList")List<Category> categoryList);


}
