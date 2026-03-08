package com.bootcamp.service.impl;

import com.bootcamp.co.ForgotPasswordCO;
import com.bootcamp.entity.user.User;
import com.bootcamp.exception.auth.ConfirmPasswordNotMatchedException;
import com.bootcamp.exception.auth.InvalidJwtTokenException;
import com.bootcamp.exception.auth.UserIsLockedException;
import com.bootcamp.exception.auth.UserNotActiveException;
import com.bootcamp.exception.user.ForgotPasswordTokenExpiredException;
import com.bootcamp.exception.user.InvalidForgotPasswordTokenException;
import com.bootcamp.exception.user.TooManyRequestException;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.BlackListedTokenService;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.ForgotPasswordService;
import com.bootcamp.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BlackListedTokenService blackListedTokenService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final static Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

    @Override
    public ApiResponse<Object> forgotPasswordMailSend(String email) {
            logger.info("Attempting to send forgot password mail for email: {}", email);

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                logger.error("User not found with email: {}", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            User user = getUser(userOptional);
            Date date = new Date();
            user.setForgotPasswordTokenTime(date);
            userRepository.save(user);

            logger.info("Forgot password token time updated for user: {}", email);

            emailService.sendForgotPasswordMail(email, date);

            logger.info("Forgot password mail sent successfully to email: {}", email);

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.forgot.password.mail.sent", null, locale));

    }

    private static User getUser(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            logger.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();
        Date previousTime = user.getForgotPasswordTokenTime();

        if (previousTime != null) {
            long timeDifference = new Date().getTime() - previousTime.getTime();
            if (timeDifference < 60 * 1000) {
                logger.warn("Too many requests for forgot password for user: {}", user.getEmail());
                throw new TooManyRequestException();
            }
        }

        if (!user.isActive()) {
            logger.warn("User is not active: {}", user.getEmail());
            throw new UserNotActiveException("User not active");
        }

        if (user.isLocked()) {
            logger.warn("User is locked: {}", user.getEmail());
            throw new UserIsLockedException("User is locked");
        }
        if(user.isDeleted()){
            logger.warn("user is deleted");
            throw new UsernameNotFoundException("");
        }
        logger.info("User found: {}", user.getEmail());
        return user;
    }

    @Override
    public ApiResponse<Object> updateForgotPassword(ForgotPasswordCO forgotPasswordCO) {
            String token = forgotPasswordCO.getForgotPasswordToken();

            if (blackListedTokenService.isTokenBlackListed(token)) {
                logger.warn("Attempt to use blacklisted token: {}", token);
                throw new InvalidJwtTokenException("Token is blacklisted");
            }

            String email =null;
            try {
                email= jwtService.extractUsername(token);
            }
            catch (ExpiredJwtException e){
                throw new ForgotPasswordTokenExpiredException();
            }
            catch (Exception e){
                throw new InvalidForgotPasswordTokenException();
            }
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                logger.error("User not found with email: {}", email);
                throw new UsernameNotFoundException("User not found");
            }

            User user = userOptional.get();
            validateUser(user);

            Date jwtIssuerTime = jwtService.extractIssuedAt(token);
            Date forgotPasswordTokenTime = user.getForgotPasswordTokenTime();

            if (Math.abs(jwtIssuerTime.getTime() - forgotPasswordTokenTime.getTime()) > 1000) {
                logger.error("Forgot password token time mismatch for user: {}", email);
                throw new InvalidForgotPasswordTokenException();
            }

            String password = forgotPasswordCO.getPassword();
            String confirmPassword = forgotPasswordCO.getConfirmPassword();

            if (!password.equals(confirmPassword)) {
                logger.warn("Password and confirm password do not match for user: {}", email);
                throw new ConfirmPasswordNotMatchedException();
            }

            user.setPassword(passwordEncoder.encode(password));
            user.setPasswordUpdateDate(LocalDate.now());
            user.setExpired(false);

            userRepository.save(user);

            Date forgotPasswordTokenExpiry = jwtService.extractExpiration(token);
            blackListedTokenService.blackListToken(token, forgotPasswordTokenExpiry);

            emailService.PasswordUpdatedMail(email);

            Locale locale = LocaleContextHolder.getLocale();
            return ResponseUtil.ok(messageSource.getMessage("message.forgot.password.updated", null, locale));
    }

    private void validateUser(User user) {
        if (!user.isActive()) {
            logger.warn("User is not active: {}", user.getEmail());
            throw new UserNotActiveException("User not active");
        }

        if (user.isLocked()) {
            logger.warn("User is locked: {}", user.getEmail());
            throw new UserIsLockedException("User is locked");
        }
    }

}
