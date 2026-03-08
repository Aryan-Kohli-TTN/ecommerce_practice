package com.bootcamp.service.impl;

import com.bootcamp.entity.user.User;
import com.bootcamp.exception.file.*;
import com.bootcamp.exception.invalidFormat.InvalidImageIdException;
import com.bootcamp.exception.product.InvalidProductIdException;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.ImageService;
import com.bootcamp.service.ImageValidationService;
import jakarta.xml.ws.RequestWrapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {


    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ImageValidationService imageValidationService;



    @Value(value = "${image.base-path}")
    String baseDirectory;
    @Value(value = "${image.user.profile-pic}")
    String userProfilePic;
    @Value(value = "${image.user.product}")
    String productImage;
    private final static Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private void validateProfileImage(MultipartFile image) {
        imageValidationService.validateImageFile(image, "Profile Image");
        logger.info("Profile image successfully validated");
    }
    @Override
    public ApiResponse<Object> saveProfilePic(MultipartFile image) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
        validateProfileImage(image);
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        String fileName = user.getId().toString() + "." + extension;
        Path uploadPath = Path.of(baseDirectory + userProfilePic);
        Path filePath = uploadPath.resolve(fileName);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Profile image uploaded successfully for user: {}", email);
        } catch (Exception e) {
            logger.error("Error uploading profile image for user {}: {}", email, e.getMessage());
            throw new FileSavingException();
        }

        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.image.profile.pic.uploaded", null, locale));
    }
    private UUID stringToUUIDImage(String id){
        UUID imageId =null;
        try{
            imageId= UUID.fromString(id);
        }
        catch (Exception e){
            throw new InvalidImageIdException();
        }
        return imageId;
    }
    @Override
    public ResponseEntity<Object> getProfileImage(String imageId) {
        UUID id = stringToUUIDImage(imageId);
        Path folderPath = Path.of(baseDirectory + userProfilePic);
        logger.info("Looking for profile image at folder: {}", folderPath.toAbsolutePath());
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");

        for (String ext : allowedExtensions) {
            Path imagePath = folderPath.resolve(id.toString() + "." + ext);
            logger.info("Checking for profile image at: {}", imagePath.toAbsolutePath());

            if (Files.exists(imagePath)) {
                try {
                    byte[] imageBytes = Files.readAllBytes(imagePath);
                    HttpHeaders headers = new HttpHeaders();

                    MediaType mediaType = switch (ext) {
                        case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                        case "png" -> MediaType.IMAGE_PNG;
                        case "bmp" -> MediaType.valueOf("image/bmp");
                        default -> MediaType.APPLICATION_OCTET_STREAM;
                    };
                    headers.setContentType(mediaType);
                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } catch (IOException e) {
                    logger.error("Error reading profile image for user ID {}: {}", id, e.getMessage());
                    throw new FileRetervingException();
                }
            }
        }

        logger.warn("Profile image not found for user ID: {}", id);
        throw new ImageNotFoundException();
    }


    @Override
    public ResponseEntity<Object> getProductVarPrimaryImage(String imageId) {
        UUID id = stringToUUIDImage(imageId);
        Path folderPath = Path.of(baseDirectory, productImage, id.toString(), "primary");
        logger.info("Looking for image at: {}", folderPath.toAbsolutePath());
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");

        for (String ext : allowedExtensions) {
            Path imagePath = folderPath.resolve("image." + ext);
            logger.info("Looking for profile image at: {}", imagePath.toAbsolutePath());

            if (Files.exists(imagePath)) {
                try {
                    byte[] imageBytes = Files.readAllBytes(imagePath);
                    HttpHeaders headers = new HttpHeaders();

                    MediaType mediaType = switch (ext) {
                        case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                        case "png" -> MediaType.IMAGE_PNG;
                        case "bmp" -> MediaType.valueOf("image/bmp");
                        default -> MediaType.APPLICATION_OCTET_STREAM;
                    };
                    headers.setContentType(mediaType);
                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } catch (IOException e) {
                    logger.error("Error reading product variation primary image with id  {}: {}", id, e.getMessage());
                    throw new FileRetervingException();
                }
            }
        }
        throw new ImageNotFoundException();
    }

    @Override
    public boolean productVarPrimaryImageExist(UUID id) {
        Path primaryFolder = Path.of(baseDirectory, productImage, id.toString(), "primary");

        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");

        for (String ext : allowedExtensions) {
            Path filePath = primaryFolder.resolve("image." + ext);
            if (Files.exists(filePath)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ApiResponse<Object> removeProfileImage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        String baseFileName = user.getId().toString();
        Path folderPath = Path.of(baseDirectory + userProfilePic);
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");

        boolean deleted = false;

        for (String ext : allowedExtensions) {
            Path imagePath = folderPath.resolve(baseFileName + "." + ext);
            if (Files.exists(imagePath)) {
                try {
                    Files.delete(imagePath);
                    logger.info("Deleted profile image: {}", imagePath);
                    deleted = true;
                    break; // stop after deleting the found file
                } catch (IOException e) {
                    logger.error("Error deleting profile image {}: {}", imagePath, e.getMessage());
                    throw new FileDeletingException();
                }
            }
        }

        if (deleted) {
            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.image.profile.pic.deleted", null, locale));
        } else {
            logger.warn("No profile image found to delete for user: {}", email);
            throw new ImageNotFoundException();
        }
    }

    @Override
    public void saveProductPrimaryImage(MultipartFile image, UUID productVariationId) {
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        Path uploadPath = Path.of(baseDirectory, productImage, productVariationId.toString(), "primary");
        Path filePath = uploadPath.resolve("image." + extension);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Product variation primary image uploaded successfully for productVariationdId: {}",
                    productVariationId);
        } catch (Exception e) {
            logger.error("Error uploading product variation primary image for id {}: {}", productVariationId, e.getMessage());
            throw new FileSavingException();
        }

//        Locale locale = LocaleContextHolder.getLocale();
//        return ResponseUtil.ok(messageSource.getMessage("message.product.variation.primary.image.done", null, locale));

    }

    @Override
    public void saveProductSecondaryImages(MultipartFile[] images, UUID productVariationId) {
        Path uploadPath = Path.of(baseDirectory, productImage, productVariationId.toString(), "secondary");

        try {
            if (Files.exists(uploadPath)) {
                Files.walk(uploadPath)
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                logger.warn("Failed to delete existing image: {}", path, e);
                            }
                        });
            }

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (int i = 0; i < images.length; i++) {
                String originalFileName = images[i].getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();

                Path filePath = uploadPath.resolve("secondary" + (i + 1) + "." + extension);

                Files.copy(images[i].getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Product variation secondary image uploaded successfully for productVariationId: {}, Image index: {}",
                        productVariationId, i);
            }
        } catch (Exception e) {
            logger.error("Error uploading product variation secondary images for id {}: {}", productVariationId, e.getMessage(), e);
            throw new FileSavingException();
        }
    }
    @Override
    public ResponseEntity<Object> getProductVarSecondaryImageByIndex(String imageId, int index) {
            UUID id = stringToUUIDImage(imageId);
        Path folderPath = Path.of(baseDirectory, productImage, id.toString(), "secondary");
        logger.info("Looking for secondary image at index {} at: {}", index, folderPath.toAbsolutePath());
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");
        for (String ext : allowedExtensions) {
            Path imagePath = folderPath.resolve("secondary" + index + "." + ext);
            if (Files.exists(imagePath)) {
                try {
                    byte[] imageBytes = Files.readAllBytes(imagePath);
                    HttpHeaders headers = new HttpHeaders();

                    MediaType mediaType = switch (ext) {
                        case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                        case "png" -> MediaType.IMAGE_PNG;
                        case "bmp" -> MediaType.valueOf("image/bmp");
                        default -> MediaType.APPLICATION_OCTET_STREAM;
                    };
                    headers.setContentType(mediaType);

                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } catch (IOException e) {
                    logger.error("Error reading secondary image at index {} for product variation with id {}: {}", index, id, e.getMessage());
                    throw new FileRetervingException();
                }
            }
        }
        throw new ImageNotFoundException();
    }

    @Override
    public boolean productVarSecondaryImageExistsByIndex(UUID id, int index) {
        Path secondaryFolder = Path.of(baseDirectory, productImage, id.toString(), "secondary");
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "bmp");
        for (String ext : allowedExtensions) {
            Path filePath = secondaryFolder.resolve("secondary" + index + "." + ext);
            if (Files.exists(filePath)) {
                return true;
            }
        }
        return false;
    }

}
