package com.bootcamp.controller;

import com.bootcamp.co.*;
import com.bootcamp.dto.CustomerProfileDTO;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping("/register")
    ResponseEntity<ApiResponse<Object>> customerRegister(@RequestBody @Valid CustomerCO customerCO){
        return new ResponseEntity<>(customerService.saveCustomer(customerCO), HttpStatus.CREATED);
    }

    @PutMapping("/activate/account")
    public ResponseEntity<Object> activateAccount(@RequestParam String token){
        return  new ResponseEntity<>(customerService.activate_customer(token),HttpStatus.OK);
    }

    @PostMapping("/new/activate/mail")
    public  ResponseEntity<Object> sendNewActivationMail( @Valid @RequestBody EmailCO emailCO){
        return new ResponseEntity<>(customerService.sendNewActivationMail(emailCO.getEmail()),HttpStatus.OK);
    }





}
