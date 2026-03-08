package com.bootcamp.controller;

import com.bootcamp.co.PasswordUpdateCO;
import com.bootcamp.service.PasswordUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@AllArgsConstructor
public class PasswordUpdateController {

    private final PasswordUpdateService passwordUpdateService;

    @PreAuthorize("hasAnyRole('SELLER','CUSTOMER','ADMIN')")
    @PatchMapping("")
    public ResponseEntity<Object> passwordUpdatePatch(@Valid @RequestBody PasswordUpdateCO passwordUpdateCO){
        return new ResponseEntity<>(passwordUpdateService.updatePassword(passwordUpdateCO), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('SELLER','CUSTOMER','ADMIN')")
    @PutMapping("")
    public ResponseEntity<Object> passwordUpdatePut(@Valid @RequestBody PasswordUpdateCO passwordUpdateCO){
        return new ResponseEntity<>(passwordUpdateService.updatePassword(passwordUpdateCO), HttpStatus.OK);
    }

}
