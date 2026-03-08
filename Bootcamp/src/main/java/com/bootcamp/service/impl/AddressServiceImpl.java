package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.co.AddressCO;
import com.bootcamp.co.AddressPatchCO;
import com.bootcamp.co.AddressPutCO;
import com.bootcamp.dto.AddressOnlyDTO;
import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.address.AddressNotFoundException;
import com.bootcamp.exception.address.DefaultAddressDeleteException;
import com.bootcamp.exception.invalidFormat.InvalidAddressIDException;
import com.bootcamp.exception.seller.SellerMultipleAddressException;
import com.bootcamp.repository.user.AddressRepository;
import com.bootcamp.repository.user.CustomerRepository;
import com.bootcamp.repository.user.SellerRepository;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.AddressService;
import com.bootcamp.utils.MyBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final SellerRepository sellerRepository;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private List<AddressOnlyDTO> addressListToDtoList(Set<Address> addresses) {
        return addresses.stream().map((address -> {
            AddressOnlyDTO addressOnlyDTO = new AddressOnlyDTO();
            BeanUtils.copyProperties(address, addressOnlyDTO);
            return addressOnlyDTO;
        })).toList();
    }
    @Override
    public ApiResponse<Object> getAddress() {
        logger.info("Fetching addresses for the user...");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("User email: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(""));
        Set<Address> addresses = user.getAddresses();
        logger.info("Found {} address(es) for user: {}", addresses.size(), email);

        return ResponseUtil.okWithData(addressListToDtoList(addresses));
    }

    @Override
    public ApiResponse<Object> saveAddress(AddressCO addressCO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.info("Saving address for user: {}", email);

        Authority authority = (Authority) authentication.getAuthorities().stream().findFirst().get();
        logger.debug("User authority: {}", authority);

        logger.info("User is a customer. Saving address...");
        return saveAddressCustomer(addressCO, email);
    }

    private ApiResponse<Object> saveAddressCustomer(AddressCO addressCO, String email) {
        logger.info("Saving address for customer with email: {}", email);

        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
        if (customerOptional.isEmpty()) {
            logger.error("User with email {} not found", email);
            throw new UsernameNotFoundException("user not found");
        }
        Customer customer = customerOptional.get();
        Address address = new Address();
        BeanUtils.copyProperties(addressCO, address, MyBeanUtils.getNullPropertyNames(addressCO));
        address.setAuditing(new Auditing());
        address.setUser(customer);
        if (customer.getAddresses().isEmpty()) {
            logger.info("First address for customer, setting as default");
            address.setIsDefaultAddress(true);
        } else if (addressCO.getIsDefaultAddress()) {
            logger.info("Setting this address as default and other addresses as not default");
            customer.getAddresses().forEach(existingAddress -> {
                existingAddress.setIsDefaultAddress(false);
                addressRepository.save(existingAddress);
            });
            address.setIsDefaultAddress(true);
        } else {
            address.setIsDefaultAddress(false);
        }
        customer.getAddresses().add(address);
        customerRepository.save(customer);
        Locale locale = LocaleContextHolder.getLocale();
        logger.info("Address saved successfully for customer: {}", email);
        return ResponseUtil.ok(messageSource.getMessage("message.address.saved", null, locale));
    }

    private ApiResponse<Object> saveAddressSeller(AddressCO addressCO, String email) {
        logger.info("Saving address for seller with email: {}", email);

        Seller seller = sellerRepository.findByEmail(email).get();
        logger.debug("Seller found with {} address(es)", seller.getAddresses().size());

        if (seller.getAddresses().size() == 1) {
            logger.error("Seller with email {} already has one address, cannot add more", email);
            throw new SellerMultipleAddressException();
        }

        Address address = new Address();
        BeanUtils.copyProperties(addressCO, address, MyBeanUtils.getNullPropertyNames(addressCO));
        address.setAuditing(new Auditing());
        address.setUser(seller);
        address.setIsDefaultAddress(true);
        seller.getAddresses().add(address);

        sellerRepository.save(seller);
        logger.info("Address saved successfully for seller: {}", email);

        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.address.saved", null, locale));
    }

    @Override
    public Address getDefaultAddress(User user) {
        logger.info("Fetching default address for user: {}", user.getEmail());
        Address defaultAddress = addressRepository.findByUserAndIsDefaultAddress(user, true);
        if (defaultAddress != null) {
            logger.info("Default address found for user: {}", user.getEmail());
        } else {
            logger.warn("No default address found for user: {}", user.getEmail());
        }
        return defaultAddress;
    }

    private UUID stringToUUIDAddress(String id) {
        UUID addressId = null;
        try {
            addressId = UUID.fromString(id);
        } catch (Exception e) {
            throw new InvalidAddressIDException();
        }
        return addressId;
    }

    @Override
    public ApiResponse<Object> deleteAddress(String id) {
        UUID addressId = stringToUUIDAddress(id);
        logger.info("Attempting to delete address with ID: {}", addressId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User with email {} not found", email);
            return new UsernameNotFoundException("");
        });

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            logger.error("Address with ID {} not found", addressId);
            throw new AddressNotFoundException();
        }

        Address address = addressOptional.get();
        logger.debug("Address found for user {}: {}", user.getEmail(), address);

        if (address.getUser().getId().equals(user.getId())) {
            if (address.getId().equals(getDefaultAddress(user).getId())) {
                logger.error("Attempted to delete the default address for user {}", user.getEmail());
                throw new DefaultAddressDeleteException();
            }
            addressRepository.delete(address);
            logger.info("Address with ID {} deleted successfully for user {}", addressId, user.getEmail());

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.address.deleted", null, locale));
        } else {
            logger.error("Access denied for user {} to delete address with ID {}", user.getEmail(), addressId);
            throw new AuthorizationDeniedException("Access Denied");
        }
    }

    @Override
    public ApiResponse<Object> updatePatchAddress(String id, AddressPatchCO addressPatchCO) {
        UUID addressId = stringToUUIDAddress(id);
        logger.info("Attempting to update address with ID: {}", addressId);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User with email {} not found", email);
            return new UsernameNotFoundException("");
        });

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            logger.error("Address with ID {} not found", addressId);
            throw new AddressNotFoundException();
        }

        Address address = addressOptional.get();
        logger.debug("Address found for user {}: {}", user.getEmail(), address);

        if (address.getUser().getId().equals(user.getId())) {
            BeanUtils.copyProperties(addressPatchCO, address, MyBeanUtils.getNullPropertyNames(addressPatchCO));
            addressRepository.save(address);
            logger.info("Address with ID {} updated successfully for user {}", addressId, user.getEmail());

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.address.updated", null, locale));
        } else {
            logger.error("Access denied for user {} to update address with ID {}", user.getEmail(), addressId);
            throw new AuthorizationDeniedException("Access Denied");
        }
    }

    @Override
    public ApiResponse<Object> updatePutAddress(String id, AddressPutCO addressPutCO) {
        UUID addressId = stringToUUIDAddress(id);

        logger.info("Attempting to update address with ID: {}", addressId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User with email {} not found", email);
            return new UsernameNotFoundException("");
        });

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            logger.error("Address with ID {} not found", addressId);
            throw new AddressNotFoundException();
        }

        Address address = addressOptional.get();
        logger.debug("Address found for user {}: {}", user.getEmail(), address);

        if (address.getUser().getId().equals(user.getId())) {
            BeanUtils.copyProperties(addressPutCO, address);
            addressRepository.save(address);
            logger.info("Address with ID {} updated successfully for user {}", addressId, user.getEmail());

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.address.updated", null, locale));
        } else {
            logger.error("Access denied for user {} to update address with ID {}", user.getEmail(), addressId);
            throw new AuthorizationDeniedException("Access Denied");
        }
    }

    @Override
    public ApiResponse<Object> setDefaultAddress(String id){
        UUID addressId = stringToUUIDAddress(id);

        logger.info("Attempting to set deafult address with ID: {}", addressId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User with email {} not found", email);
            return new UsernameNotFoundException("");
        });

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            logger.error("Address with ID {} not found", addressId);
            throw new AddressNotFoundException();
        }

        Address address = addressOptional.get();
        logger.debug("Address found for user {}: {}", user.getEmail(), address);
        Locale locale = LocaleContextHolder.getLocale();
        if(address.getIsDefaultAddress()){
            return ResponseUtil.ok(messageSource.getMessage("message.already.default.address",null,locale));
        }
        else{
            user.getAddresses().forEach((allAddress -> {
                allAddress.setIsDefaultAddress(allAddress.getId().equals(address.getId()));
                addressRepository.save(allAddress);
            }));
        }
        return ResponseUtil.ok(messageSource.getMessage("message.default.address.updated",null,locale));
    }


}
