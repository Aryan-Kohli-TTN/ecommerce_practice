package com.bootcamp.dto;

import com.bootcamp.entity.user.Address;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminProfileDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean isActive;
    private Address address;
    private String profilePic;
}
