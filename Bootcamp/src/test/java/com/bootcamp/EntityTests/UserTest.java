package com.bootcamp.EntityTests;

import com.bootcamp.entity.user.*;
import com.bootcamp.enums.Authority;
import com.bootcamp.repository.role.RoleRepository;
import com.bootcamp.repository.user.AddressRepository;
import com.bootcamp.repository.user.CustomerRepository;
import com.bootcamp.repository.user.SellerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class UserTest {
    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void create_roles(){
        Role role_admin = new Role(Authority.ROLE_ADMIN);
        roleRepository.save(role_admin);
        Role role_seller = new Role(Authority.ROLE_SELLER);
        roleRepository.save(role_seller);
        Role role_customer = new Role(Authority.ROLE_CUSTOMER);
        roleRepository.save(role_customer);
    }
    @Test
    public void test_create_seller(){
        for(int i=0;i<6;i++){
            Seller seller = new Seller();
            seller.setEmail("email"+i+"@gmail.com");
            seller.setCompanyName("company"+i);
            seller.setFirstName("first"+i);
            seller.setMiddleName("middle"+i);
            seller.setLastName("last"+i);
            seller.setPassword("password"+i);
            seller.setGstNo("gstno"+i);
            seller.setCompanyContactNumber("companyContact"+i);
            seller.setActive(true);
            seller.setLocked(false);
            seller.setDeleted(false);
            seller.setExpired(false);
            seller.setInvalidAttemptCount(0);
            seller.setPasswordUpdateDate(LocalDate.now());
            seller.setRole(roleRepository.findRoleByAuthority(Authority.ROLE_SELLER));
            sellerRepository.save(seller);
            System.out.println(seller);
        }
    }

    @Test
    public void test_create_customer(){
        for(int i=10;i<=15;i++){
            Customer customer = new Customer();
            customer.setEmail("email"+i+"@gmail.com");
            customer.setFirstName("first"+i);
            customer.setMiddleName("middle"+i);
            customer.setLastName("last"+i);
            customer.setPassword("password"+i);
            customer.setDeleted(false);
            customer.setActive(false);
            customer.setExpired(false);
            customer.setLocked(false);
            customer.setInvalidAttemptCount(0);
            customer.setPasswordUpdateDate(LocalDate.now());
            customer.setCustomerContactNumber("contact"+i);
            customer.setRole(roleRepository.findRoleByAuthority(Authority.ROLE_CUSTOMER));
            customerRepository.save(customer);
        }
    }

    @Test
    public void test_save_address(){
        for(int i=0;i<6;i++){
            Seller user = sellerRepository.findByFirstName("first"+i).get();
            Address  address = new Address();
            address.setCity("city"+i);
            address.setState("state"+i);
            address.setCountry("country"+i);
            address.setAddressLine("Add.line"+i);
            address.setZipCode("zipcode"+i);
            address.setLabel("label"+i);
            address.setUser(user);
            addressRepository.save(address);
        }
    }


}
