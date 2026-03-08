package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.auth.ConfirmPasswordNotMatchedException;
import com.bootcamp.exception.seller.DuplicateCompanyNameException;
import com.bootcamp.exception.seller.DuplicateGstNoException;
import com.bootcamp.exception.user.UserAlreadyExistException;
import com.bootcamp.repository.role.RoleRepository;
import com.bootcamp.co.SellerCO;
import com.bootcamp.repository.user.AddressRepository;
import com.bootcamp.repository.user.SellerRepository;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.SellerService;
import com.bootcamp.utils.MyBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
@Service
@AllArgsConstructor
public class SellerServiceImpl implements SellerService {
    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final MessageSource messageSource;

    private void validateSeller(SellerCO sellerCO){

        if (!sellerCO.getConfirmPassword().equals(sellerCO.getPassword())) {
            logger.error("Password and Confirm Password do not match for seller with email: {}", sellerCO.getEmail());
            throw new ConfirmPasswordNotMatchedException();
        }

        if (userRepository.existsByEmail(sellerCO.getEmail())) {
            logger.error("User already exists with email: {}", sellerCO.getEmail());
            throw new UserAlreadyExistException();
        }

        if (sellerRepository.existsByGstNo(sellerCO.getGstNo())) {
            logger.error("Duplicate GST No. detected for seller with email: {}", sellerCO.getEmail());
            throw new DuplicateGstNoException();
        }

        if (sellerRepository.existsByCompanyName(sellerCO.getCompanyName())) {
            logger.error("Duplicate Company Name detected for seller with email: {}", sellerCO.getEmail());
            throw new DuplicateCompanyNameException();
        }
    }

    @Override
    public ApiResponse<Object> saveSeller(SellerCO sellerCO) {
            logger.info("Starting the seller registration process for email: {}", sellerCO.getEmail());
            validateSeller(sellerCO);

            Seller seller = new Seller();
            BeanUtils.copyProperties(sellerCO, seller, MyBeanUtils.getNullPropertyNames(sellerCO));
            seller.setPassword(passwordEncoder.encode(sellerCO.getPassword()));
            seller.setPasswordUpdateDate(LocalDate.now());
            seller.setActivationTokenTime(new Date());

            Address address = new Address();
            BeanUtils.copyProperties(sellerCO.getAddress(), address);
            address.setAuditing(new Auditing());
            address.setUser(seller);
            seller.getAddresses().add(address);

            Role role = roleRepository.findRoleByAuthority(Authority.ROLE_SELLER);
            seller.setRole(role);

            Auditing auditing = new Auditing();
            seller.setAuditing(auditing);

            role.addUser(seller);

            sellerRepository.save(seller);
            roleRepository.save(role);

            emailService.sendSellerRegisteredEmail(sellerCO.getEmail());

            logger.info("Seller registration successful for email: {}", sellerCO.getEmail());

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.seller.registered", null, locale));


    }
}
