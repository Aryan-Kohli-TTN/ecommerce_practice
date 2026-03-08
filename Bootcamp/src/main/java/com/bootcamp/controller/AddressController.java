package com.bootcamp.controller;


import com.bootcamp.co.AddressCO;
import com.bootcamp.co.AddressPatchCO;
import com.bootcamp.co.AddressPutCO;
import com.bootcamp.service.AddressService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@AllArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PreAuthorize("hasAnyRole('SELLER','CUSTOMER')")
    @GetMapping("")
    public ResponseEntity<Object> reteriveAddress(){
        return new ResponseEntity<Object>(addressService.getAddress(),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteAddress(@PathVariable String id){
        return  new ResponseEntity<Object>(addressService.deleteAddress(id),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    @PatchMapping("/{id}")
    ResponseEntity<Object> updateAddressPatch(@PathVariable String id,@Valid @RequestBody AddressPatchCO addressPatchCO){
        return  new ResponseEntity<>(addressService.updatePatchAddress(id, addressPatchCO),HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    @PutMapping("/{id}")
    ResponseEntity<Object> updateAddressPut(@PathVariable String id,@Valid @RequestBody AddressPutCO addressPutCO){
        return  new ResponseEntity<>(addressService.updatePutAddress(id, addressPutCO),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{id}/default")
    ResponseEntity<Object> setDefaultAddress(@PathVariable String id){
        return  new ResponseEntity<>(addressService.setDefaultAddress(id),HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("")
    ResponseEntity<Object> saveAddress(@Valid @RequestBody AddressCO addressCO){
        return  new ResponseEntity<>(addressService.saveAddress(addressCO),HttpStatus.OK);
    }
}
