package com.bootcamp.repository.user;

import com.bootcamp.entity.user.Address;
import com.bootcamp.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Address findByUser(User user);

    Address findByUserAndIsDefaultAddress(User user , boolean IsDefaultAddress);
}
