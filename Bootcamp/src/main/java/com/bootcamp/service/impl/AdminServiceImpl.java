package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.co.CustomerCO;
import com.bootcamp.co.SellerCO;
import com.bootcamp.dto.*;
import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.address.AddressNotFoundException;
import com.bootcamp.exception.auth.ConfirmPasswordNotMatchedException;
import com.bootcamp.exception.invalidFormat.InvalidAddressIDException;
import com.bootcamp.exception.invalidFormat.InvalidUserIDException;
import com.bootcamp.exception.user.UserAlreadyExistException;
import com.bootcamp.repository.role.RoleRepository;
import com.bootcamp.repository.user.AddressRepository;
import com.bootcamp.repository.user.CustomerRepository;
import com.bootcamp.repository.user.SellerRepository;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.*;
import com.bootcamp.service.ParamsValidationsService;
import com.bootcamp.utils.MyBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final AddressService addressService;
    private final SellerService sellerService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MessageSource messageSource;
    private final ParamsValidationsService paramsValidationsService;


    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private UUID stringToUUIDUser(String id){
        UUID userID = null;
        try{
            userID=UUID.fromString(id);
        }
        catch (Exception e){
            throw new InvalidUserIDException();
        }
        return  userID;
    }
    private UUID stringToUUIDAddress(String id){
        UUID addressId = null;
        try{
            addressId=UUID.fromString(id);
        }
        catch (Exception e){
            throw new InvalidAddressIDException();
        }
        return  addressId;
    }
    @Override
    public ApiResponse<Object> activateUser(String id) {
        UUID user_id = stringToUUIDUser(id);
        logger.info("Attempting to activate user with ID: {}", user_id);

        Locale locale = LocaleContextHolder.getLocale();
        User user = userRepository.findById(user_id).orElseThrow(() -> {
            logger.error("User with ID {} not found", user_id);
            return new UsernameNotFoundException("");
        });

        if (user.isActive()) {
            logger.info("User with ID {} is already active", user_id);
            return ResponseUtil.ok(messageSource.getMessage("message.user.already.activated", null, locale));
        }

        user.setActive(true);
        userRepository.save(user);
        logger.info("User with ID {} activated successfully", user_id);

        emailService.AccountActivatedSuccesfullyMail(user.getEmail());
        logger.info("Activation email sent to user with ID {}", user_id);

        return ResponseUtil.ok(messageSource.getMessage("message.user.activated.now", null, locale));
    }

    @Override
    public ApiResponse<Object> deactivateUser(String id) {
        UUID user_id = stringToUUIDUser(id);
        logger.info("Attempting to deactivate user with ID: {}", user_id);

        Locale locale = LocaleContextHolder.getLocale();
        User user = userRepository.findById(user_id).orElseThrow(() -> {
            logger.error("User with ID {} not found", user_id);
            return new UsernameNotFoundException("");
        });

        if (!user.isActive()) {
            logger.info("User with ID {} is already deactivated", user_id);
            return ResponseUtil.ok(messageSource.getMessage("message.user.already.deactivated", null, locale));
        }

        user.setActive(false);
        userRepository.save(user);
        logger.info("User with ID {} deactivated successfully", user_id);

        emailService.AccountDeactivatedSuccesfullyMail(user.getEmail());
        logger.info("Deactivation email sent to user with ID {}", user_id);

        return ResponseUtil.ok(messageSource.getMessage("message.user.deactivated.now", null, locale));
    }



    @Override
    public ApiResponse<Object> getUserWithAddress(String id) {
        UUID user_id = stringToUUIDUser(id);
        logger.info("Fetching user with ID: {} and their addresses", user_id);

        User user = userRepository.findById(user_id).orElseThrow(() -> {
            logger.error("User with ID {} not found", user_id);
            return new UsernameNotFoundException("");
        });

        logger.info("User with ID {} found. Returning addresses", user_id);
        return ResponseUtil.okWithData(user.getAddresses());
    }

    @Override
    public ApiResponse<Object> getUserWithoutAddress(String id) {
        UUID user_id = stringToUUIDUser(id);
        logger.info("Fetching user with ID: {} without addresses", user_id);

        User user = userRepository.findById(user_id).orElseThrow(() -> {
            logger.error("User with ID {} not found", user_id);
            return new UsernameNotFoundException("");
        });

        logger.info("User with ID {} found. Returning user data", user_id);
        return ResponseUtil.okWithData(user);
    }

    @Override
    public ApiResponse<Object> getAddressWithoutUser(String id) {
        UUID address_id = stringToUUIDAddress(id);
        logger.info("Fetching address with ID: {}", address_id);

        Address address = addressRepository.findById(address_id).orElseThrow(() -> {
            logger.error("Address with ID {} not found", address_id);
            return new AddressNotFoundException();
        });

        logger.info("Address with ID {} found. Returning address data", address_id);
        return ResponseUtil.okWithData(address);
    }

    @Override
    public ApiResponse<Object> getAddressWithUser(String id) {
        UUID address_id = stringToUUIDAddress(id);
        logger.info("Fetching address with ID: {}", address_id);

        Address address = addressRepository.findById(address_id).orElseThrow(() -> {
            logger.error("Address with ID {} not found", address_id);
            return new AddressNotFoundException();
        });

        logger.info("Address with ID {} found. Creating AddressDTO", address_id);
        AddressDTO addressDTO = new AddressDTO();
        BeanUtils.copyProperties(address, addressDTO);
        addressDTO.setUser(address.getUser());

        logger.info("AddressDTO created successfully for address ID: {}", address_id);
        return ResponseUtil.okWithData(addressDTO);
    }

    @Override
    public ApiResponse<Object> getAllCustomers(String Size, String Offset, String orderBy, String sortBy, String email, boolean withAddress) {
        logger.info("Fetching all customers with parameters - Size: {}, Offset: {}, SortBy: {}, Email: {}, WithAddress: {}", Size, Offset, sortBy, email, withAddress);

        int pageSize = paramsValidationsService.getPageSize(Size);
        int pageOffset = paramsValidationsService.getPageOffset(Offset);
        sortBy= paramsValidationsService.getSortBy(sortBy,Arrays.asList("id","email","firstName","middleName","lastName",
                "createdAt","updatedAt","auditing.createdAt","auditing.updatedAt","customerContactNumber"));
        Sort.Direction direction= paramsValidationsService.getOrderBy(orderBy);

        logger.info("Fetching customers with email containing '{}', sorted by '{}', page size: {}, page offset: {}", email, sortBy, pageSize, pageOffset);
        List<Customer> customers = customerRepository.findByEmailContaining(email, PageRequest.of(pageOffset, pageSize, Sort.by(direction,sortBy))).getContent();

        List<CustomerProfileDTO> result = customers.stream().map(customer -> {
            CustomerProfileDTO customerProfileDTO = new CustomerProfileDTO();
            BeanUtils.copyProperties(customer, customerProfileDTO);

            if (withAddress) {
                logger.info("Fetching default address for customer with ID: {}", customer.getId());
                Address address = addressService.getDefaultAddress(customer);
                customerProfileDTO.setAddress(address);
            }

            customerProfileDTO.setProfilePic("http://localhost:8080/api/image/get/profile-pic/" + customer.getId());
            return customerProfileDTO;
        }).toList();

        logger.info("Returning {} customer profiles", result.size());
        return ResponseUtil.okWithData(result);
    }



    @Override
    public ApiResponse<Object> getAllSellers(String Size, String Offset, String orderBy, String sortBy, String email) {
        logger.info("Fetching all sellers with parameters - Size: {}, Offset: {}, SortBy: {}, Email: {}", Size, Offset, sortBy, email);

        int pageSize = paramsValidationsService.getPageSize(Size);
        int pageOffset = paramsValidationsService.getPageOffset(Offset);
        sortBy= paramsValidationsService.getSortBy(sortBy,Arrays.asList("id", "email", "firstName", "middleName", "lastName",
                "createdAt", "updatedAt", "auditing.createdAt", "auditing.updatedAt", "companyContactNumber", "gstNumber", "companyName"));
        Sort.Direction direction= paramsValidationsService.getOrderBy(orderBy);
        logger.info("Fetching sellers with email containing '{}', sorted by '{}', page size: {}, page offset: {}", email, sortBy, pageSize, pageOffset);
        List<Seller> sellers = sellerRepository.findByEmailContaining(email, PageRequest.of(pageOffset, pageSize, Sort.by(direction,sortBy))).getContent();

        List<SellerProfileDTO> result = sellers.stream().map(seller -> {
            SellerProfileDTO sellerProfileDTO = new SellerProfileDTO();
            BeanUtils.copyProperties(seller, sellerProfileDTO);

            logger.info("Fetching default address for seller with ID: {}", seller.getId());
            Address address = addressService.getDefaultAddress(seller);
            sellerProfileDTO.setAddress(address);

            sellerProfileDTO.setProfilePic("http://localhost:8080/api/image/get/profile-pic/" + seller.getId());
            return sellerProfileDTO;
        }).toList();

        logger.info("Returning {} seller profiles", result.size());
        return ResponseUtil.okWithData(result);
    }


    @Override
    public ApiResponse<Object> addManyCustomers(List<CustomerCO> customers) {
        logger.info("Adding many customers, total count: {}", customers.size());
        customers.forEach((this::validateCustomer));
        customers.forEach(customer -> {
            logger.info("Adding customer with email: {}", customer.getEmail());
            saveCustomerwithActivation(customer);
        });

        Locale locale = LocaleContextHolder.getLocale();
        logger.info("Successfully added many customers.");
        return ResponseUtil.ok(messageSource.getMessage("message.many.customers.added", null, locale));
    }
    private void validateCustomer(CustomerCO customerCO){
        if (!customerCO.getConfirmPassword().equals(customerCO.getPassword())) {
            logger.error("Password and confirm password do not match for email: {}", customerCO.getEmail());
            throw new ConfirmPasswordNotMatchedException(customerCO.getEmail());
        }

        if (userRepository.existsByEmail(customerCO.getEmail())) {
            logger.error("User already exists with email: {}", customerCO.getEmail());
            throw new UserAlreadyExistException(customerCO.getEmail());
        }
    }
    private void validateSeller(SellerCO sellerCO){
        if (!sellerCO.getConfirmPassword().equals(sellerCO.getPassword())) {
            logger.error("Password and confirm password do not match for email: {}", sellerCO.getEmail());
            throw new ConfirmPasswordNotMatchedException(sellerCO.getEmail());
        }

        if (userRepository.existsByEmail(sellerCO.getEmail())) {
            logger.error("User already exists with email: {}", sellerCO.getEmail());
            throw new UserAlreadyExistException(sellerCO.getEmail());
        }
    }
    private void saveCustomerwithActivation(CustomerCO customerCO) {
        logger.info("Saving customer with email: {}", customerCO.getEmail());
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerCO, customer, MyBeanUtils.getNullPropertyNames(customerCO));
        customer.setPassword(passwordEncoder.encode(customerCO.getPassword()));
        customer.setPasswordUpdateDate(LocalDate.now());

        customer.setActive(true);
        Date date = new Date();
        customer.setActivationTokenTime(date);

        Role role = roleRepository.findRoleByAuthority(Authority.ROLE_CUSTOMER);
        customer.setRole(role);
        role.addUser(customer);

        Auditing auditing = new Auditing();
        customer.setAuditing(auditing);

        logger.info("Saving customer with email: {}", customerCO.getEmail());
        customerRepository.save(customer);
        roleRepository.save(role);

        logger.info("Successfully saved customer with email: {}", customerCO.getEmail());
    }
    @Override
    public ApiResponse<Object> addManySellers(List<SellerCO> sellers) {
        logger.info("Adding many sellers, total count: {}", sellers.size());
        sellers.forEach(this::validateSeller);
        sellers.forEach(seller -> {
            logger.info("Adding seller with email: {}", seller.getEmail());
            sellerService.saveSeller(seller);
        });

        Locale locale = LocaleContextHolder.getLocale();
        logger.info("Successfully added many sellers.");
        return ResponseUtil.ok(messageSource.getMessage("message.many.sellers.added", null, locale));
    }
}
