package com.bootcamp.dto;

import com.bootcamp.co.AddressCO;
import com.bootcamp.entity.user.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerProfileDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String customerContactNumber;
    private boolean isActive;
    private Address address;
    private String profilePic;
}
