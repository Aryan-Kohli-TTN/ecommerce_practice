package com.bootcamp.controller;

import com.bootcamp.co.CategoryFieldCO;
import com.bootcamp.service.CategoryFieldService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category/field")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class CategoryFieldController {

    private final CategoryFieldService categoryFieldService;

    @PostMapping("")
    ResponseEntity<Object> addCategoryField(@RequestBody @Valid CategoryFieldCO categoryFieldCO){
        return new ResponseEntity<>(categoryFieldService.addCategoryField(categoryFieldCO)
                , HttpStatus.OK);
    }
    @PostMapping("/many")
    ResponseEntity<Object> addManyCategoryField(@Valid @RequestBody List<CategoryFieldCO> categoryFieldCOList){
        return new ResponseEntity<>(categoryFieldService.addManyCategoryField(categoryFieldCOList)
                , HttpStatus.OK);
    }
    @GetMapping("")
    ResponseEntity<Object> getAllCategoryField(@RequestParam(required = false,defaultValue = "10") String pageSize,
                                               @RequestParam(required = false,defaultValue = "0") String pageOffset,
                                               @RequestParam(required = false,defaultValue = "auditing.createdAt")String sortBy,
                                               @RequestParam(required = false,defaultValue = "asc")String orderBy,
                                               @RequestParam(required = false,defaultValue = "")String name){
        return new ResponseEntity<>(categoryFieldService.getAllCategoryField(pageSize,pageOffset,sortBy,orderBy,name)
                , HttpStatus.OK);
    }
}
