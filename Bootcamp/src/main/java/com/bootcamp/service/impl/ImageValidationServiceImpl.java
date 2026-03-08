package com.bootcamp.service.impl;

import com.bootcamp.exception.file.FileInvalidFormatException;
import com.bootcamp.exception.file.FileIsEmptyException;
import com.bootcamp.exception.file.FileTooLargeException;
import com.bootcamp.service.ImageValidationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class ImageValidationServiceImpl implements ImageValidationService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png","image/jpg","image/bmp");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    @Override
    public void validateImageFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new FileIsEmptyException(fieldName);
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileInvalidFormatException(fieldName);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException(fieldName);
        }
    }
}
