package com.bootcamp.service.impl;

import com.bootcamp.co.AdminProfileUpdateCO;
import com.bootcamp.co.CommonProfileUpdatePatchCO;
import com.bootcamp.co.CustomerProfileUpdateCO;
import com.bootcamp.co.SellerProfileUpdateCO;
import com.bootcamp.dto.AdminProfileDTO;
import com.bootcamp.dto.CustomerProfileDTO;
import com.bootcamp.dto.SellerProfileDTO;
import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.seller.DuplicateCompanyNameException;
import com.bootcamp.exception.seller.DuplicateGstNoException;
import com.bootcamp.exception.invalidFormat.InvalidRequestBodyException;
import com.bootcamp.repository.user.CustomerRepository;
import com.bootcamp.repository.user.SellerRepository;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.AddressService;
import com.bootcamp.service.ProfileService;
import com.bootcamp.utils.MyBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final  CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public ApiResponse<Object> getProfile() {
            logger.info("Fetching profile information");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
            logger.info("Authenticated user: {}", email);

            if (authority.equals(Authority.ROLE_CUSTOMER)) {
                logger.info("User role is CUSTOMER. Retrieving customer profile.");
                return getProfileCustomer(email);
            } else if (authority.equals(Authority.ROLE_SELLER)){
                logger.info("User role is SELLER. Retrieving seller profile.");
                return getProfileSeller(email);
            }
            else {
                logger.info("User role is Admin. Retrieving seller profile.");
                return getProfileAdmin(email);
            }
    }

    private ApiResponse<Object> getProfileCustomer(String email) {
            logger.info("Fetching customer profile for email: {}", email);

            Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("Customer not found with email: {}", email);
                return new UsernameNotFoundException("Customer not found");
            });

            CustomerProfileDTO customerProfileDTO = new CustomerProfileDTO();
            BeanUtils.copyProperties(customer, customerProfileDTO);

            Address address = addressService.getDefaultAddress(customer);
            customerProfileDTO.setAddress(address);
            customerProfileDTO.setProfilePic("http://localhost:8080/api/image/get/profile-pic/" + customer.getId());

            logger.info("Customer profile retrieved successfully for email: {}", email);
            return ResponseUtil.okWithData(customerProfileDTO);
    }



    private ApiResponse<Object> getProfileSeller(String email) {
            logger.info("Fetching seller profile for email: {}", email);

            Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("Seller not found with email: {}", email);
                return new UsernameNotFoundException("Seller not found");
            });

            SellerProfileDTO sellerProfileDTO = new SellerProfileDTO();
            BeanUtils.copyProperties(seller, sellerProfileDTO);

            Address address = addressService.getDefaultAddress(seller);
            sellerProfileDTO.setAddress(address);
            sellerProfileDTO.setProfilePic("http://localhost:8080/api/image/get/profile-pic/" + seller.getId());

            logger.info("Seller profile retrieved successfully for email: {}", email);
            return ResponseUtil.okWithData(sellerProfileDTO);
    }
    private ApiResponse<Object> getProfileAdmin(String email) {
            logger.info("Fetching admin profile for email: {}", email);

            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found");
            });

            AdminProfileDTO adminProfileDTO = new AdminProfileDTO();
            BeanUtils.copyProperties(user, adminProfileDTO);


            adminProfileDTO.setProfilePic("http://localhost:8080/api/image/get/profile-pic/" + user.getId());

            logger.info("Admin profile retrieved successfully for email: {}", email);
            return ResponseUtil.okWithData(adminProfileDTO);
    }



    @Override
    public ApiResponse<Object> updateProfile(CommonProfileUpdatePatchCO commonProfileUpdatePatchCO) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();

            if (authority.equals(Authority.ROLE_CUSTOMER)) {
                logger.info("User is a CUSTOMER. Validating profile update request.");
                if (commonProfileUpdatePatchCO.getCompanyName() != null || commonProfileUpdatePatchCO.getCompanyContactNumber() != null
                        || commonProfileUpdatePatchCO.getGstNo() != null) {
                    logger.error("Invalid fields in customer profile update request");
                    throw new InvalidRequestBodyException();
                }

                CustomerProfileUpdateCO customerProfileUpdateCO = new CustomerProfileUpdateCO();
                BeanUtils.copyProperties(commonProfileUpdatePatchCO, customerProfileUpdateCO, MyBeanUtils.getNullPropertyNames(commonProfileUpdatePatchCO));
                return updateProfileCustomer(customerProfileUpdateCO);
            } else if (authority.equals(Authority.ROLE_SELLER)) {
                logger.info("User is a SELLER. Validating profile update request.");
                if (commonProfileUpdatePatchCO.getCustomerContactNumber() != null) {
                    logger.error("Invalid field in seller profile update request");
                    throw new InvalidRequestBodyException();
                }

                SellerProfileUpdateCO sellerProfileUpdateCO = new SellerProfileUpdateCO();
                BeanUtils.copyProperties(commonProfileUpdatePatchCO, sellerProfileUpdateCO, MyBeanUtils.getNullPropertyNames(commonProfileUpdatePatchCO));
                return updateProfileSeller(sellerProfileUpdateCO);
            }
            else {
                logger.info("User is a admin. Validating profile update request.");
                if (commonProfileUpdatePatchCO.getCompanyName() != null || commonProfileUpdatePatchCO.getCompanyContactNumber() != null
                        || commonProfileUpdatePatchCO.getGstNo() != null || commonProfileUpdatePatchCO.getCustomerContactNumber()!=null) {
                    logger.error("Invalid fields in admin profile update request");
                    throw new InvalidRequestBodyException();
                }
                AdminProfileUpdateCO adminProfileUpdateCO = new AdminProfileUpdateCO();
                BeanUtils.copyProperties(commonProfileUpdatePatchCO, adminProfileUpdateCO, MyBeanUtils.getNullPropertyNames(commonProfileUpdatePatchCO));
                return updateProfileAdmin(adminProfileUpdateCO);
            }
    }

    @Override
    public ApiResponse<Object> updateProfileCustomer(CustomerProfileUpdateCO customerProfileUpdateCO) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Updating profile for customer with email: {}", email);

            Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("Customer not found with email: {}", email);
                return new UsernameNotFoundException("Customer not found");
            });

            BeanUtils.copyProperties(customerProfileUpdateCO, customer, MyBeanUtils.getNullPropertyNames(customerProfileUpdateCO));
            customerRepository.save(customer);

            logger.info("Customer profile updated successfully for email: {}", email);
            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.customer.profile.updated", null, locale));
    }
    private ApiResponse<Object> updateProfileAdmin(AdminProfileUpdateCO adminProfileUpdateCO){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->{
            logger.error("admin not found with email: {}", email);
            return new UsernameNotFoundException("admuin not found");
        });
        BeanUtils.copyProperties(adminProfileUpdateCO,user,MyBeanUtils.getNullPropertyNames(adminProfileUpdateCO));
        userRepository.save(user);

        logger.info("Admin profile updated successfully for email: {}", email);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.admin.profile.updated", null, locale));

    }
    @Override
    public ApiResponse<Object> updateProfileSeller(SellerProfileUpdateCO sellerProfileUpdateCO) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Updating profile for seller with email: {}", email);

            Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("Seller not found with email: {}", email);
                return new UsernameNotFoundException("Seller not found");
            });

            if (sellerProfileUpdateCO.getGstNo() != null && sellerRepository.existsByGstNo(sellerProfileUpdateCO.getGstNo())) {
                logger.error("Duplicate GST No. detected for seller with email: {}", email);
                throw new DuplicateGstNoException();
            }

            if (sellerProfileUpdateCO.getCompanyName() != null && sellerRepository.existsByCompanyName(sellerProfileUpdateCO.getCompanyName())) {
                logger.error("Duplicate Company Name detected for seller with email: {}", email);
                throw new DuplicateCompanyNameException();
            }

            BeanUtils.copyProperties(sellerProfileUpdateCO, seller, MyBeanUtils.getNullPropertyNames(sellerProfileUpdateCO));
            sellerRepository.save(seller);

            logger.info("Seller profile updated successfully for email: {}", email);
            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.seller.profile.updated", null, locale));
    }

}
