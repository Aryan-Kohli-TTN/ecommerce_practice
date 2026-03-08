package com.bootcamp.scheduler;

import com.bootcamp.entity.user.User;
import com.bootcamp.repository.token.BlackListedTokenRepository;
import com.bootcamp.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class UserUnlockScheduler {

    private final UserRepository userRepository;

    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    public UserUnlockScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 20_000) // runs every 1 min
    public void unlockExpiredLockedUsers() {
        List<User> lockedUsers = userRepository.findByIsLockedTrue();
        LocalDateTime now = LocalDateTime.now();
        for (User user : lockedUsers) {
            LocalDateTime updatedAt = user.getAuditing().getUpdatedAt();

            if (updatedAt != null && updatedAt.plusMinutes(1).isBefore(now)) {
                user.setLocked(false);
                user.setInvalidAttemptCount(0);
            }
        }
        userRepository.saveAll(lockedUsers);
    }
}
