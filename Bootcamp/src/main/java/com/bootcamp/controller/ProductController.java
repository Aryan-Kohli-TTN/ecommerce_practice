package com.bootcamp.controller;

import com.bootcamp.co.ProductSaveCO;
import com.bootcamp.co.ProductUpdateCO;
import com.bootcamp.co.ProductVariationSaveCO;
import com.bootcamp.co.ProductVariationUpdateCO;
import com.bootcamp.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("")
    ResponseEntity<Object> saveProduct(@RequestBody @Valid ProductSaveCO productSaveCO){
        return new ResponseEntity<>(productService.saveProduct(productSaveCO), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteProduct(@PathVariable String id){
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/variation")
    ResponseEntity<Object> saveProductVariation(@ModelAttribute ProductVariationSaveCO productVariationSaveCO){
        return new ResponseEntity<>(productService.saveProductVariation(productVariationSaveCO), HttpStatus.OK);
    }

    //variations of a given product id
    @PreAuthorize("hasAnyRole('SELLER','CUSTOMER','ADMIN')")
    @GetMapping("/variation/productId")
    ResponseEntity<Object> getAllProductVariation(@RequestParam Map<String,String> allParams){
        return new ResponseEntity<>(productService.getAllProductVariation(allParams), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/similar/{id}")
    ResponseEntity<Object> getSimilarProduct(@RequestParam(required = false,defaultValue = "10") String pageSize,
                                                  @RequestParam(required = false,defaultValue = "0") String pageOffset,
                                                  @RequestParam (required = false ,defaultValue = "id")String sortBy,
                                                  @RequestParam(required = false,defaultValue = "asc")String orderBy,
                                                  @PathVariable String id){
        return new ResponseEntity<>(productService.getSimilarProduct(id,pageSize,pageOffset,orderBy,sortBy), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/variation/{id}")
    ResponseEntity<Object> getParticularVariation(@PathVariable String id){
        return new ResponseEntity<>(productService.getOneProductVariation(id), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/variation/{id}")
    ResponseEntity<Object> updateProductVariation(@PathVariable String id,@ModelAttribute @Valid ProductVariationUpdateCO productVariationUpdateCO){
        return new ResponseEntity<>(productService.updateProductVariation(id,productVariationUpdateCO), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    ResponseEntity<Object> getAllProducts(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<>(productService.getAllProducts(allParams), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    ResponseEntity<Object> getOneProduct(@PathVariable String id) {
        return new ResponseEntity<>(productService.getOneProduct(id), HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    ResponseEntity<Object> updateProduct(@PathVariable String id, @RequestBody @Valid ProductUpdateCO productUpdateCO) {
        return new ResponseEntity<>(productService.updateProduct(id,productUpdateCO), HttpStatus.OK);
    }

    @PostMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> activateProduct(@PathVariable String id) {
        return new ResponseEntity<>(productService.activateProduct(id), HttpStatus.OK);
    }

    @PostMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> deactivateProduct(@PathVariable String id) {
        return new ResponseEntity<>(productService.deactivateProduct(id), HttpStatus.OK);
    }

}
