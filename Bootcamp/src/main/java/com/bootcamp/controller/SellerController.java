package com.bootcamp.controller;

import com.bootcamp.co.*;
import com.bootcamp.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@AllArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    @PostMapping("/register")
    ResponseEntity<Object> seller_register(@Valid @RequestBody SellerCO sellerCO){
        return new ResponseEntity<>(sellerService.saveSeller(sellerCO), HttpStatus.CREATED);
    }


}
