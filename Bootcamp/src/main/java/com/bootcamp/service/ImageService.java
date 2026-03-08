package com.bootcamp.service;

import com.bootcamp.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    ApiResponse<Object> saveProfilePic(MultipartFile image);

    ResponseEntity<Object> getProfileImage(String id);

    ApiResponse<Object> removeProfileImage();

    void saveProductPrimaryImage(MultipartFile image, UUID productVariationId);

    void saveProductSecondaryImages(MultipartFile[] images,UUID productVariationId);

    ResponseEntity<Object> getProductVarPrimaryImage(String id);

    boolean productVarPrimaryImageExist(UUID id);

    ResponseEntity<Object> getProductVarSecondaryImageByIndex(String id, int index);
    boolean productVarSecondaryImageExistsByIndex(UUID id, int index);
}