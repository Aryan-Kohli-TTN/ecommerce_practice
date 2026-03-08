package com.bootcamp.controller;

import com.bootcamp.co.CategoryCO;
import com.bootcamp.co.CategoryUpdateCO;
import com.bootcamp.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> addCategory(@Valid @RequestBody CategoryCO categoryCO){
        return new ResponseEntity<>(categoryService.addCategory(categoryCO), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> updateCategory(@Valid @RequestBody CategoryUpdateCO categoryUpdateCO,@PathVariable String id){
        return new ResponseEntity<>(categoryService.updateCategory(id,categoryUpdateCO), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    // for customers with no filters
    ResponseEntity<Object> getOneCategory(@Valid @PathVariable String id){
        return new ResponseEntity<>(categoryService.getOneCategory(id), HttpStatus.OK);
    }
    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    ResponseEntity<Object> getAllCategory(@RequestParam(required = false,defaultValue = "10") String pageSize,
                                          @RequestParam(required = false,defaultValue = "0") String pageOffset,
                                          @RequestParam (required = false ,defaultValue = "id")String sortBy,
                                          @RequestParam(required = false,defaultValue = "") String categoryName,
                                          @RequestParam(required = false,defaultValue = "asc")String orderBy){
        return new ResponseEntity<>(categoryService.getAllCategory(pageSize,pageOffset,orderBy,sortBy,categoryName), HttpStatus.OK);
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<Object> getCategoryDetails(@Valid @PathVariable String id){
        return new ResponseEntity<>(categoryService.getCategoryInfo(id),HttpStatus.OK);
    }
}
