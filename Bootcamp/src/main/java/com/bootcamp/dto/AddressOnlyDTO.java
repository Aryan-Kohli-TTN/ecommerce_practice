package com.bootcamp.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressOnlyDTO {
    private UUID id;
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
    private Boolean isDefaultAddress;
    private String label;
}
