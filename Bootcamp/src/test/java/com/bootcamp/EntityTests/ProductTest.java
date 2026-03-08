package com.bootcamp.EntityTests;

import com.bootcamp.entity.category.Category;
import com.bootcamp.entity.product.Product;
import com.bootcamp.entity.product.ProductVariation;
import com.bootcamp.entity.user.Seller;
import com.bootcamp.entity.user.User;
import com.bootcamp.repository.category.CategoryRepository;
import com.bootcamp.repository.product.ProductRepository;
import com.bootcamp.repository.product.ProductVariationRepository;
import com.bootcamp.repository.user.SellerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.BigInteger;

@SpringBootTest
public class ProductTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductVariationRepository productVariationRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CategoryRepository categoryRepository;
    @Test
    public void test_product_create(){
        Seller seller = sellerRepository.findByFirstName("first0").get();
        Category category = categoryRepository.findByCategoryName("MobilePhone").get();
        for(int i=0;i<5;i++){
            Product product = new Product();
            product.setProductName("name"+i);
            product.setProductDescription("desc"+i);
            product.setProductBrand("brand"+i);
            product.setCancellable(true);
            product.setReturnable(true);
            product.setActive(false);
            product.setDeleted(false);
            product.setSeller((Seller) seller);
            product.setCategory(category);
            productRepository.save(product);
//            System.out.println(product);
        }
    }

    @Test
    public void test_create_Product_variation(){
        // creating 3 variations of each product
            for(int i=0;i<5;i++){
                Product product = productRepository.findByProductName("name"+i);
                for(int j=0;j<3;j++){
                    ProductVariation productVariation = new ProductVariation();
                    productVariation.setProductVariationQuantity(BigInteger.valueOf(10000*j+j));
                    productVariation.setProductVariationPrice(BigDecimal.valueOf(190.22*j-j));
                    productVariation.setActive(true);
                    productVariation.setProduct(product);
                    String jsonData = "{\"key1\":\"value1\",\"key2\":123,\"key3\":true}";
                    productVariation.setMetaData(jsonData);
                    productVariationRepository.save(productVariation);
                }
            }
    }

}
