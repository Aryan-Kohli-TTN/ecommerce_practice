package com.bootcamp.repository.product;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.product.Product;
import com.bootcamp.entity.user.Seller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {


    
    Product findByProductName(String productName);
    boolean existsByProductNameAndCategoryAndProductBrandAndSeller(String productName,Category category, String productBrand, Seller seller);

    List<Product> findBySellerAndProductNameContainingAndProductBrandContaining(Seller seller, String productName,String productBrand, Pageable pageable);

    Optional<Product> findBySellerAndId(Seller seller , UUID productId);

    @Query(value = "select * from product where product_id=:id",nativeQuery = true)
    Optional<Product> getProductForAdmin(@Param("id") UUID productId);

    @Query(value = "Select p from Product p where p.category IN :categoryList and p.isActive=true and SIZE(p.productVariationList)>0")
    List<Product> getAllProductCustomer(@Param("categoryList")List<Category> categoryList,Pageable pageable);

    List<Product> findBySeller(Seller seller,Pageable pageable);
    List<Product> findByCategory(Category category ,Pageable pageable);
    List<Product> findBySellerAndCategory(Seller seller,Category category,Pageable pageable);


//    @Query(value = "SELECT p from Product p where ")
//    List<Product> getAllProductsForAdmin(Seller seller,Category category,Pageable pageable);

    List<Product> findByProductBrandAndIdNot(String brandName,UUID id,Pageable pageable);


    @Query("Select DISTINCT p.productBrand from Product p where p.category IN :categoryList")
    List<String> getAllBrandsFromCategory(@Param("categoryList") List<Category> categories);

    @Query("Select p.seller from Product p  where p.id = :id")
    Optional<Seller> getSellerOfProduct(@Param("id")UUID productId);
}
