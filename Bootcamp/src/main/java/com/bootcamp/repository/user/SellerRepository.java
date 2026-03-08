package com.bootcamp.repository.user;

import com.bootcamp.entity.user.Customer;
import com.bootcamp.entity.user.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {

    public Optional<Seller> findByCompanyName(String companyName);
    public Optional<Seller> findByFirstName(String firstName);
    public Optional<Seller> findByLastName(String LastName);
    public Optional<Seller> findByEmail(String email);
    public boolean existsByGstNo(String gstNo);
    public boolean existsByCompanyName(String gstNo);

    Page<Seller> findByEmailContaining(String email, Pageable pageable);

}
