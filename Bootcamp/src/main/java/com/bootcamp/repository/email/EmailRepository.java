package com.bootcamp.repository.email;

import com.bootcamp.entity.email.EmailDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<EmailDetails, UUID> {
}
