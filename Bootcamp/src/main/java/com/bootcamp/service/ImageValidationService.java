package com.bootcamp.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageValidationService {
    void validateImageFile(MultipartFile file, String fieldName);
}
