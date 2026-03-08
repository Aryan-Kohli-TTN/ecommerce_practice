package com.bootcamp.controller;

import com.bootcamp.co.*;
import com.bootcamp.exception.invalidFormat.InvalidAddressIDException;
import com.bootcamp.exception.invalidFormat.InvalidUserIDException;
import com.bootcamp.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminUserController {

    private final AdminService adminService;


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate/{id}")
    ResponseEntity<Object> activateUser(@PathVariable String id){
        return new ResponseEntity<>(adminService.activateUser(id),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate/{id}")
    ResponseEntity<Object> deactivateUser(@PathVariable String id){
        return new ResponseEntity<>(adminService.deactivateUser(id),HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers")
    ResponseEntity<Object> getAllCustomers(@RequestParam(required = false,defaultValue = "10") String pageSize,
                                           @RequestParam(required = false,defaultValue = "0") String pageOffset,
                                           @RequestParam (required = false ,defaultValue = "id")String sortBy,
                                           @RequestParam(required = false,defaultValue = "@") String email,
                                           @RequestParam(required = false,defaultValue = "false") boolean withAddress,
                                            @RequestParam(required = false,defaultValue = "asc")String orderBy
                                            ){

        return new ResponseEntity<>(adminService.getAllCustomers(pageSize,pageOffset,orderBy,sortBy,email,withAddress),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sellers")
    ResponseEntity<Object> getAllSellers(@RequestParam(required = false,defaultValue = "10") String pageSize,
                                           @RequestParam(required = false,defaultValue = "0") String pageOffset,
                                           @RequestParam (required = false ,defaultValue = "id")String sortBy,
                                           @RequestParam(required = false,defaultValue = "@") String email,
                                         @RequestParam(required = false,defaultValue = "asc")String orderBy){
        return new ResponseEntity<>(adminService.getAllSellers(pageSize,pageOffset,orderBy,sortBy,email),HttpStatus.OK);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    ResponseEntity<Object> getUserWithoutAddress(@PathVariable String id){
        return new ResponseEntity<>(adminService.getUserWithoutAddress(id),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{id}/address")
    ResponseEntity<Object> getUserWithAddress(@PathVariable String id){
        return new ResponseEntity<>(adminService.getUserWithAddress(id),HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/address/{id}")
    ResponseEntity<Object> getAddressWithoutUser(@PathVariable String id){
        return new ResponseEntity<>(adminService.getAddressWithoutUser(id),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/address/{id}/user")
    ResponseEntity<Object> getAddressWithUser(@PathVariable String id){
        return new ResponseEntity<>(adminService.getAddressWithUser(id),HttpStatus.OK);
    }


    // adding many customers for testing filtering
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers/add/all")
    ResponseEntity<Object> addManyCustomers(@RequestBody @Valid List<CustomerCO> customerCOList){
        return new ResponseEntity<>(adminService.addManyCustomers(customerCOList),HttpStatus.OK);
    }
    // adding many sellers for testing filtering
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sellers/add/all")
    ResponseEntity<Object> addManySellers(@RequestBody @Valid List<SellerCO> sellerCOList){
        return new ResponseEntity<>(adminService.addManySellers(sellerCOList),HttpStatus.OK);
    }


}
