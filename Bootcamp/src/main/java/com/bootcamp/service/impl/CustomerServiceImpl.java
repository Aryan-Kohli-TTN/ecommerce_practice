package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.exception.auth.ConfirmPasswordNotMatchedException;
import com.bootcamp.exception.auth.OldActivationTokenException;
import com.bootcamp.exception.customer.InvalidActivationTokenException;
import com.bootcamp.exception.user.TooManyRequestException;
import com.bootcamp.exception.user.UserAlreadyActivatedException;
import com.bootcamp.exception.user.UserAlreadyExistException;
import com.bootcamp.repository.role.RoleRepository;
import com.bootcamp.repository.user.CustomerRepository;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.co.CustomerCO;
import com.bootcamp.response.*;
import com.bootcamp.service.CustomerService;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.JWTService;
import com.bootcamp.utils.MyBeanUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    public ApiResponse<Object> saveCustomer(CustomerCO customerCO) {
        if (!customerCO.getConfirmPassword().equals(customerCO.getPassword())) {
            logger.error("Confirm password does not match for email: {}", customerCO.getEmail());
            throw new ConfirmPasswordNotMatchedException();
        }

        if (userRepository.existsByEmail(customerCO.getEmail())) {
            logger.error("User with email already exists: {}", customerCO.getEmail());
            throw new UserAlreadyExistException();
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerCO, customer, MyBeanUtils.getNullPropertyNames(customerCO));
        customer.setPassword(passwordEncoder.encode(customerCO.getPassword()));
        customer.setPasswordUpdateDate(LocalDate.now());

        Date date = new Date();
        customer.setActivationTokenTime(date);

        Role role = roleRepository.findRoleByAuthority(Authority.ROLE_CUSTOMER);
        customer.setRole(role);
        role.addUser(customer);

        Auditing auditing = new Auditing();
        customer.setAuditing(auditing);

        customerRepository.save(customer);
        roleRepository.save(role);

        emailService.sendActivateAccountMail(customer.getEmail(), date);

        logger.info("Customer successfully registered with email: {}", customerCO.getEmail());

        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.customer.registered", null, locale));
    }
    @Override
    public ApiResponse<Object> sendNewActivationMail(String email) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
        if (customerOptional.isEmpty()) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("user not found");
        }

        Date date = new Date();
        Customer customer = customerOptional.get();

        if (customer.isActive()) {
            logger.warn("Attempted to send activation email to already active user: {}", email);
            throw new UserAlreadyActivatedException();
        }

        Date previousTime = customer.getActivationTokenTime();
        long timeDifference = new Date().getTime() - previousTime.getTime();

        if (timeDifference < 60 * 1000) {
            logger.warn("Too many activation email requests for user: {}", email);
            throw new TooManyRequestException();
        }

        customer.setActivationTokenTime(date);
        customerRepository.save(customer);
        emailService.sendActivateAccountMail(email, date);

        logger.info("Activation email sent to user: {}", email);

        Locale locale = LocaleContextHolder.getLocale();
        return ResponseUtil.ok(messageSource.getMessage("message.customer.activation.mail", null, locale));
    }
    @Override
    public ApiResponse<Object> activate_customer(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty()) {
                logger.error("User not found with email: {}", email);
                throw new UsernameNotFoundException("user not found");
            }

            long time = jwtService.extractIssuedAt(token).getTime();
                 /*
                Database is storing like 2025-04-02 15:23:22.293
                Jwt is having issuedTime like Wed Apr 02 15:23:22 IST 2025
                due to the differences like .293 i have used to take difference of 1000milliseconds
            * */
            if (Math.abs(user.get().getActivationTokenTime().getTime() - time) > 1000) {
                logger.warn("Activation token expired or altered for user: {}", email);
                throw new OldActivationTokenException();
            }

            if (user.get().isActive()) {
                logger.info("User already activated: {}", email);
                throw new UserAlreadyActivatedException();
            }

            user.get().setActive(true);
            userRepository.save(user.get());
            emailService.AccountActivatedSuccesfullyMail(email);

            logger.info("User successfully activated: {}", email);

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.customer.successfully.activated", null, locale));

        } catch (ExpiredJwtException e) {
            String email = e.getClaims().getSubject();
            long time = e.getClaims().getIssuedAt().getTime();

            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty()) {
                logger.error("User not found with email: {}", email);
                throw new UsernameNotFoundException("user not found");
            }

            if (Math.abs(user.get().getActivationTokenTime().getTime() - time) > 1000) {
                logger.warn("Activation token expired or altered for user: {}", email);
                throw new OldActivationTokenException();
            }

            if (user.get().isActive()) {
                logger.info("User already activated: {}", email);
                throw new UserAlreadyActivatedException();
            }

            Date date = new Date();
            user.get().setActivationTokenTime(date);
            userRepository.save(user.get());
            emailService.sendActivateAccountMail(email, date);

            logger.info("Activation email resent to user: {}", email);

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.customer.activation.mail.again", null, locale));

        } catch (Exception e) {
            logger.error("Error during activation process: ", e.getMessage());
            throw new InvalidActivationTokenException();
        }
    }


//    public ApiResponse<O>
}
