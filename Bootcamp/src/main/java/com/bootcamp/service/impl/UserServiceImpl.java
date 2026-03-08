package com.bootcamp.service.impl;

import com.bootcamp.entity.user.User;
import com.bootcamp.entity.user.UserInfoDetail;
import com.bootcamp.repository.user.UserRepository;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.JWTService;
import com.bootcamp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final EmailService emailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent() && !user.get().isDeleted()) {
                LocalDate passwordUpdateDate = user.get().getPasswordUpdateDate();
                boolean expired = ChronoUnit.DAYS.between(passwordUpdateDate, LocalDate.now()) > 15;
                user.get().setExpired(expired);
                userRepository.save(user.get());
//                logger.info("User found and expired status updated for username: {}", username);
                return new UserInfoDetail(
                        user.get().getEmail(),
                        user.get().getPassword(),
                        user.get().isActive(),
                        user.get().isLocked(),
                        List.of(user.get().getRole().getAuthority()),
                        expired
                );
            }
            logger.warn("User not found for username: {}", username);
            throw new UsernameNotFoundException("user not found");
    }


    @Override
    public void setPasswordCountZero(UserDetails userDetails) {
            User user = userRepository.findByEmail(userDetails.getUsername()).get();
            user.setInvalidAttemptCount(0);
            userRepository.save(user);
            logger.info("Reset invalid attempt count to zero for user: {}", userDetails.getUsername());
    }

    @Override
    public void increaseCountPassword(UserDetails userDetails) {

            User user = userRepository.findByEmail(userDetails.getUsername()).get();
            user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);
            if (user.getInvalidAttemptCount() == 3) {
                user.setLocked(true);
                emailService.sendAccountLockEmail(user.getEmail());
                logger.info("User account locked due to 3 invalid password attempts: {}", userDetails.getUsername());
            }
            userRepository.save(user);
            logger.info("Increased invalid attempt count for user: {}", userDetails.getUsername());
    }

}
