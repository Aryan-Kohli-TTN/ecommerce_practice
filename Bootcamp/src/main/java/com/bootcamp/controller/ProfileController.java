package com.bootcamp.controller;


import com.bootcamp.co.CommonProfileUpdatePatchCO;
import com.bootcamp.co.CustomerProfileUpdateCO;
import com.bootcamp.co.SellerProfileUpdateCO;
import com.bootcamp.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    @GetMapping("")
    ResponseEntity<Object> getProfile(){
        return new ResponseEntity<>(profileService.getProfile(), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER','ADMIN')")
    @PatchMapping("")
    ResponseEntity<Object> patchUpdateProfile(@Valid @RequestBody CommonProfileUpdatePatchCO commonProfileUpdatePatchCO){
        return new ResponseEntity<>(profileService.updateProfile(commonProfileUpdatePatchCO),HttpStatus.OK);
    }
}
