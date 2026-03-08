package com.bootcamp.controller;

import com.bootcamp.co.CategoryFieldValuesListCO;
import com.bootcamp.service.CategoryFieldValuesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category/field/values")
@AllArgsConstructor
public class CategoryFieldValuesController {

    private final CategoryFieldValuesService categoryFieldValuesService;


    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> addFieldValues(@RequestBody @Valid CategoryFieldValuesListCO categoryFieldValuesListCO){
        return new ResponseEntity<>(categoryFieldValuesService.addFieldValues(categoryFieldValuesListCO), HttpStatus.OK);
    }
    @PutMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> updateFieldValues(@RequestBody @Valid CategoryFieldValuesListCO categoryFieldValuesListCO){
        return new ResponseEntity<>(categoryFieldValuesService.updateFieldValues(categoryFieldValuesListCO), HttpStatus.OK);
    }
}
