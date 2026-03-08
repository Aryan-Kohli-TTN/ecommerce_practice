package com.bootcamp.co;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerProfileUpdateCO {

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.firstName.pattern}")
    private String firstName;


    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.middleName.pattern}")
    @Size(min = 3, max = 50, message = "{validations.middleName.size}")
    private String middleName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.lastName.pattern}")
    private String lastName;

    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;


    @Pattern(message = "{validations.companyContactNumber.pattern}", regexp = "^\\+[0-9]{1,3} ?[6-9]\\d{9}$")
    private String companyContactNumber;

    @Pattern(message = "{validations.gstNo.pattern}",
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9]{1}Z[a-zA-Z0-9]{1}$")
    private String gstNo;

    @Pattern(
            regexp = "^[A-Za-z0-9][A-Za-z0-9 .-]{2,24}$",
            message = "{validations.companyName.pattern}"
    )
    @Size(min = 3, max = 25,message = "{validations.companyName.size}")
    private String companyName;
}
