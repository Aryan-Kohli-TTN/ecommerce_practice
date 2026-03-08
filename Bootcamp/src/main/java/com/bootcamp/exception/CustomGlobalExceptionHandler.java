package com.bootcamp.exception;

import com.bootcamp.exception.address.AddressNotFoundException;
import com.bootcamp.exception.address.DefaultAddressDeleteException;
import com.bootcamp.exception.auth.*;
import com.bootcamp.exception.category.*;
import com.bootcamp.exception.customer.InvalidActivationTokenException;
import com.bootcamp.exception.email.EmailSendingException;
import com.bootcamp.exception.file.*;
import com.bootcamp.exception.invalidFormat.*;
import com.bootcamp.exception.paging.InvalidOrderByException;
import com.bootcamp.exception.paging.InvalidPageOffsetException;
import com.bootcamp.exception.paging.InvalidPageSizeException;
import com.bootcamp.exception.paging.InvalidSortByException;
import com.bootcamp.exception.product.*;
import com.bootcamp.exception.seller.DuplicateCompanyNameException;
import com.bootcamp.exception.seller.DuplicateGstNoException;
import com.bootcamp.exception.seller.SellerMultipleAddressException;
import com.bootcamp.exception.user.*;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(ConfirmPasswordNotMatchedException.class)
    public final ResponseEntity<Object> exception(ConfirmPasswordNotMatchedException ex, WebRequest request, Locale locale)
            throws Exception {
        if (ex.getUsername() == null) {
            ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("error.confirm.password", null, locale));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } else {
            ApiResponse<Object> apiResponse =
                    ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST,
                            messageSource.getMessage("error.confirm.password.username", new Object[]{ex.getUsername()}, locale));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, errors.toString());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.NOT_FOUND, messageSource.getMessage("error.user.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OldActivationTokenException.class)
    public final ResponseEntity<Object> handleOldActivationToken(OldActivationTokenException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.old.activation.token", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyActivatedException.class)
    public final ResponseEntity<Object> handleUserAlreadyActivated(UserAlreadyActivatedException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.OK, messageSource.getMessage("error.user.already.activated", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public final ResponseEntity<Object> handleInvalidJwtToken(InvalidJwtTokenException ex, WebRequest request, Locale locale) {
//        System.out.println(ex.getMessage());
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.UNAUTHORIZED, messageSource.getMessage("error.invalid.jwt.token", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> handleMethodArguementTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request, Locale locale) {
        String param = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        Class<?> expectedType = ex.getRequiredType();

        String message;

        if (expectedType == UUID.class) {
            message = messageSource.getMessage("error.invalid.uuid.format", new Object[]{param, value}, locale);
        } else if (expectedType == Integer.class || expectedType == Long.class) {
            message = messageSource.getMessage("error.invalid.number.format", new Object[]{param, value}, locale);
        } else {
            message = messageSource.getMessage("error.invalid.value.format", new Object[]{param, value}, locale);
        }
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, message);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public final ResponseEntity<Object> handleExpiredJwtToken(ExpiredJwtException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.UNAUTHORIZED, messageSource.getMessage("error.jwt.token.expired", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<Object> handleUserAlreadyExist(UserAlreadyExistException ex, WebRequest request, Locale locale) {
        if (ex.getUsername() == null) {
            ApiResponse<Object> apiResponse =
                    ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.user.already.exist", null, locale));
            return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
        } else {
            ApiResponse<Object> apiResponse =
                    ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.user.already.exist.username", new Object[]{ex.getUsername()}, locale));
            return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(NegativeNumberException.class)
    public final ResponseEntity<Object> handleNegativeNumberExist(NegativeNumberException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.negative.number", new Object[]{ex.getMessage()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public final ResponseEntity<Object> handleImageNotFoundException(ImageNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.image.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSortByException.class)
    public final ResponseEntity<Object> handleInvalidSortBy(InvalidSortByException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.sort.by", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SellerMultipleAddressException.class)
    public final ResponseEntity<Object> handleSellerMultipleAddressException(SellerMultipleAddressException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.seller.multiple.address", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public final ResponseEntity<Object> handleInvalidRefreshToken(InvalidRefreshTokenException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.refresh.token", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidForgotPasswordTokenException.class)
    public final ResponseEntity<Object> handleInvalidForgotPasswordToken(InvalidForgotPasswordTokenException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.forgot.password.token", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public final ResponseEntity<Object> handleAuthorizationDenied(AuthorizationDeniedException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.FORBIDDEN, messageSource.getMessage("error.access.denied", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    public final ResponseEntity<Object> handleInvalidRequestBody(InvalidRequestBodyException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.request.body", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public final ResponseEntity<Object> handleAddressNotFound(AddressNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.address.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForgotPasswordTokenExpiredException.class)
    public final ResponseEntity<Object> handleForgotPasswordTokenExpired(ForgotPasswordTokenExpiredException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.forgot.password.token.expired", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public final ResponseEntity<Object> handleRefreshTokenExpired(RefreshTokenExpiredException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.refresh.token.expired", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.bad.credentials", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIsLockedException.class)
    public final ResponseEntity<Object> handleUserIsLockedException(UserIsLockedException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.LOCKED, messageSource.getMessage("error.user.locked", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.LOCKED);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public final ResponseEntity<Object> handleUserNotActiveException(UserNotActiveException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.FORBIDDEN, messageSource.getMessage("error.user.not.active", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidActivationTokenException.class)
    public final ResponseEntity<Object> handleInvalidActivationToken(InvalidActivationTokenException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.FORBIDDEN, messageSource.getMessage("error.invalid.activation.token", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserLoggedOutException.class)
    public final ResponseEntity<Object> handleUserLoggedOutException(UserLoggedOutException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.UNAUTHORIZED, messageSource.getMessage("error.user.logged.out", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    // it comes when there are no params
    @Override
    public final ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Locale locale = request.getLocale();
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.missing.servlet.request.parameter", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DuplicateGstNoException.class)
    public final ResponseEntity<Object> handleDuplicateGstNo(DuplicateGstNoException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.duplicate.gstno", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // url not found
    @Override
    public final ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Locale locale = request.getLocale();
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.NOT_FOUND, messageSource.getMessage("error.url.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateCompanyNameException.class)
    public final ResponseEntity<Object> handleDuplicateCompanyName(DuplicateCompanyNameException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.duplicate.company.name", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefaultAddressDeleteException.class)
    public final ResponseEntity<Object> handleDefaultAddressDelete(DefaultAddressDeleteException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.default.address.delete", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUUIDException.class)
    public final ResponseEntity<Object> handleInvalidUUIDException(InvalidUUIDException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.uuid.invalid.format", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public final ResponseEntity<Object> handleCredentailsExpired(CredentialsExpiredException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.UNAUTHORIZED, messageSource.getMessage("error.credentials.expired", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TooManyRequestException.class)
    public final ResponseEntity<Object> handleTooManyRequestException(TooManyRequestException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.too.many.request", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // it comes when body is empty
    @Override
    public final ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Locale locale = request.getLocale();
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.http.message.not.readable", null, locale));

        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPageOffsetException.class)
    public final ResponseEntity<Object> handleInvalidPageOffsetException(InvalidPageOffsetException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.page.offset", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPageSizeException.class)
    public final ResponseEntity<Object> handleInvalidPageSizeException(InvalidPageSizeException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.page.size", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<Object> handleIllegalArguementException(IllegalArgumentException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.illegal.arguement", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryFieldAlreadyExistException.class)
    public final ResponseEntity<Object> handleCategoryFieldAlreadyExistException(CategoryFieldAlreadyExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.field.exist", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderByException.class)
    public final ResponseEntity<Object> handleInvalidOrderByException(InvalidOrderByException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.order.by", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidParentCategoryIdException.class)
    public final ResponseEntity<Object> handleInvalidParentCategoryIdException(InvalidParentCategoryIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.parent.category.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RootCategoryAlreadyExistException.class)
    public final ResponseEntity<Object> handleRootCategoryAlreadyExistException(RootCategoryAlreadyExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.root.category.already.exist", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParentCategoryHasProductException.class)
    public final ResponseEntity<Object> handleParentCategoryHasProductException(ParentCategoryHasProductException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.parent.category.has.product", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryExistAtDepthException.class)
    public final ResponseEntity<Object> handleCategoryExistAtDepthException(CategoryExistAtDepthException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.already.exist.depth", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryExistAtWidthException.class)
    public final ResponseEntity<Object> handleCategoryExistAtWidthException(CategoryExistAtWidthException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.already.exist.width", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCategoryFieldIdException.class)
    public final ResponseEntity<Object> handleInvalidCategoryFieldIdException(InvalidCategoryFieldIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.field.id", new Object[]{ex.getCategoryFieldID()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCategoryIdException.class)
    public final ResponseEntity<Object> handleInvalidCategoryIdException(InvalidCategoryIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.category.id", new Object[]{ex.getCategoryId()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryFieldValuesAlreadyExistException.class)
    public final ResponseEntity<Object> handleCategoryFieldValuesAlreadyExistException(CategoryFieldValuesAlreadyExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.field.values.already.exist", new Object[]{ex.getCategoryId(), ex.getFieldId()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateMetaDataValuesException.class)
    public final ResponseEntity<Object> handleDuplicateMetaDataValuesException(DuplicateMetaDataValuesException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.metadata.duplicate.values", new Object[]{ex.getDuplicateValues(), ex.getFieldID()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MetadataMinCountException.class)
    public final ResponseEntity<Object> handleMetadataMinCountException(MetadataMinCountException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.metadata.min.count", new Object[]{ex.getId()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryFieldValuesNotFoundException.class)
    public final ResponseEntity<Object> handleCategoryFieldValuesNotFoundException(CategoryFieldValuesNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.NOT_FOUND, messageSource.getMessage("error.field.values.not.found", new Object[]{ex.getFieldId()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SomeMetaFieldsExistException.class)
    public final ResponseEntity<Object> handleSomeMetaFieldsExistException(SomeMetaFieldsExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.some.meta.data.exist.conflict", new Object[]{ex.getDuplicateValues(), ex.getFieldId()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MetaDataNonLeafCategoryException.class)
    public final ResponseEntity<Object> handleMetaDataNonLeafCategoryException(MetaDataNonLeafCategoryException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.meta.data.non.leaf.category", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public final ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyExistException.class)
    public final ResponseEntity<Object> handleProductAlreadyExistException(ProductAlreadyExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.already.exist", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryIsNonLeafException.class)
    public final ResponseEntity<Object> handleCategoryIsNonLeafException(CategoryIsNonLeafException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.category.non.leaf", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public final ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.NOT_FOUND, messageSource.getMessage("error.product.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotActiveException.class)
    public final ResponseEntity<Object> handleProductNotActiveException(ProductNotActiveException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.NOT_FOUND, messageSource.getMessage("error.product.not.active", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public final ResponseEntity<Object> handleFileTooLargeException(FileTooLargeException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.file.too.large", new Object[]{ex.getFileName()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileInvalidFormatException.class)
    public final ResponseEntity<Object> handleFileInvalidFormatException(FileInvalidFormatException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.file.invalid.format", new Object[]{ex.getFileName()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileIsEmptyException.class)
    public final ResponseEntity<Object> handleFileIsEmptyException(FileIsEmptyException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.file.is.empty", new Object[]{ex.getFileName()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductMetadataMinCountException.class)
    public final ResponseEntity<Object> handleProductMetadataMinCountException(ProductMetadataMinCountException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.meta.data.min.count", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFieldValuesException.class)
    public final ResponseEntity<Object> handleInvalidFieldValuesException(InvalidFieldValuesException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.field.values", new Object[]{ex.getField()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldNotAssociatedException.class)
    public final ResponseEntity<Object> handleFieldNotAssociatedException(FieldNotAssociatedException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.field.not.associated", new Object[]{ex.getField()}, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFieldsException.class)
    public final ResponseEntity<Object> handleInvalidFieldsException(InvalidFieldsException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.invalid.field", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public final ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.json.processing", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductVariationAlreadyExistException.class)
    public final ResponseEntity<Object> handleProductVariationAlreadyExistException(ProductVariationAlreadyExistException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.variation.already.exist", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VariationStructureNotMatchException.class)
    public final ResponseEntity<Object> handleVariationStructureNotMatchException(VariationStructureNotMatchException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.variation.invalid.structure", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotAssociatedWithSellerException.class)
    public final ResponseEntity<Object> handleProductNotAssociatedWithSellerException(ProductNotAssociatedWithSellerException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.not.associated.with.seller", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecondaryImageMaxException.class)
    public final ResponseEntity<Object> handleSecondaryImageMaxException(SecondaryImageMaxException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.secondary.image.max.count", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductVariationNotFoundException.class)
    public final ResponseEntity<Object> handleProductVariationNotFoundException(ProductVariationNotFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.variation.not.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyDeactivated.class)
    public final ResponseEntity<Object> handleProductAlreadyDeactivated(ProductAlreadyDeactivated ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.already.deactivated", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyActivated.class)
    public final ResponseEntity<Object> handleProductAlreadyActivated(ProductAlreadyActivated ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.already.activated", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInActiveException.class)
    public final ResponseEntity<Object> handleProductInActiveException(ProductInActiveException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.inactive", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoProductVariationException.class)
    public final ResponseEntity<Object> handleNoProductVariationException(NoProductVariationException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.product.no.variations", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryIsRequiredException.class)
    public final ResponseEntity<Object> handleCategoryIsRequiredException(CategoryIsRequiredException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.categoryId.required", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMinPriceException.class)
    public final ResponseEntity<Object> handleInvalidMinPriceException(InvalidMinPriceException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.min.price", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMaxPriceException.class)
    public final ResponseEntity<Object> handleInvalidMaxPriceException(InvalidMaxPriceException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.max.price", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoProductFoundException.class)
    public final ResponseEntity<Object> handleNoProductFoundException(NoProductFoundException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.no.product.found", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public final ResponseEntity<Object> handleInvalidProductIdException(InvalidProductIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.product.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductVariationIdException.class)
    public final ResponseEntity<Object> handleInvalidProductVariationIdException(InvalidProductVariationIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.product.variation.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserIDException.class)
    public final ResponseEntity<Object> handleInvalidUserIDException(InvalidUserIDException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.user.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAddressIDException.class)
    public final ResponseEntity<Object> handleInvalidAddressIDException(InvalidAddressIDException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.address.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidImageIdException.class)
    public final ResponseEntity<Object> handleInvalidImageIdException(InvalidImageIdException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.invalid.image.id", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileSavingException.class)
    public final ResponseEntity<Object> handleFileSavingException(FileSavingException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("error.file.saving", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileRetervingException.class)
    public final ResponseEntity<Object> handleFileRetervingException(FileRetervingException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("error.file.reterving", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileDeletingException.class)
    public final ResponseEntity<Object> handleFileDeletingException(FileDeletingException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("error.file.deleting", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailSendingException.class)
    public final ResponseEntity<Object> handleEmailSendingException(EmailSendingException ex, WebRequest request, Locale locale) {
        ApiResponse<Object> apiResponse =
                ResponseUtil.errorStatus(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage("error.email.sending", null, locale));
        return new ResponseEntity<Object>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}