package com.bootcamp.service.impl;

import com.bootcamp.co.PasswordUpdateCO;
import com.bootcamp.entity.user.User;
import com.bootcamp.exception.auth.ConfirmPasswordNotMatchedException;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.PasswordUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
@AllArgsConstructor
public class PasswordUpdateServiceImpl implements PasswordUpdateService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(PasswordUpdateServiceImpl.class);
    @Override
    public ApiResponse<Object> updatePassword(PasswordUpdateCO passwordUpdateCO) {
            logger.info("Starting password update process");

            if (!passwordUpdateCO.getPassword().equals(passwordUpdateCO.getConfirmPassword())) {
                logger.error("Password and confirm password do not match");
                throw new ConfirmPasswordNotMatchedException();
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.info("Updating password for user with email: {}", email);

            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });

            user.setPassword(passwordEncoder.encode(passwordUpdateCO.getPassword()));
            user.setPasswordUpdateDate(LocalDate.now());
            user.setExpired(false);
            userRepository.save(user);

            logger.info("Password updated successfully for email: {}", email);

            emailService.PasswordUpdatedMail(email);

            Locale locale = LocaleContextHolder.getLocale();
            logger.info("Password update response sent to user: {}", email);

            return ResponseUtil.ok(messageSource.getMessage("message.password.updated", null, locale));
    }

}
