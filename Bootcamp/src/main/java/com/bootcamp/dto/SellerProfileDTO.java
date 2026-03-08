package com.bootcamp.dto;

import com.bootcamp.entity.user.Address;
import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SellerProfileDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean isActive;
    private Address address;
    private String gstNo;
    private String companyName;
    private String companyContactNumber;
    private String profilePic;

}
