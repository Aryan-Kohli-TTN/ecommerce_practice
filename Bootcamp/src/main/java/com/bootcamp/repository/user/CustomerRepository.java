package com.bootcamp.repository.user;

import com.bootcamp.dto.CustomerProfileDTO;
import com.bootcamp.entity.user.Customer;
import com.bootcamp.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByFirstName(String firstName);
    Optional<Customer> findByLastName(String LastName);
     Optional<Customer> findByEmail(String email);
     Page<Customer> findByEmailContaining(String email, Pageable pageable);
}
