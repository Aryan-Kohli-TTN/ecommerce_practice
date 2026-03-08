package com.bootcamp.controller;

import com.bootcamp.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/image")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/get/profile-pic/{id}")
    ResponseEntity<Object> getProfilePic(@PathVariable String id){
        return imageService.getProfileImage(id);
    }

    @PostMapping("/upload/profile-pic")
    ResponseEntity<Object> uploadProfilePic(@RequestParam("image") MultipartFile image){
        return new ResponseEntity<Object>(imageService.saveProfilePic(image), HttpStatus.OK);
    }

    @DeleteMapping("/delete/profile-pic")
    ResponseEntity<Object> removeProfileImage(){
        return new ResponseEntity<Object>(imageService.removeProfileImage(), HttpStatus.OK);
    }

    @GetMapping(value = "/product-variation/primary/{id}")
    ResponseEntity<Object> getProductVariationPrimaryImage(@PathVariable String id){
        return  imageService.getProductVarPrimaryImage(id);
    }
    @GetMapping(value = "/product-variation/secondary/{id}/{index}")
    ResponseEntity<Object> getProductVariationPrimaryImage(@PathVariable String id,@PathVariable int index){
        return imageService.getProductVarSecondaryImageByIndex(id,index);
    }
}
