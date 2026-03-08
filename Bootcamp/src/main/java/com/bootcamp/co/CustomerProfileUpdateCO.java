package com.bootcamp.co;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerProfileUpdateCO {

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.firstName.pattern}")
    private String firstName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.middleName.pattern}")
    private String middleName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.lastName.pattern}")
    private String lastName;

    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;


    @Pattern(message = "{validations.customerContactNumber.pattern}",
            regexp = "^\\+\\d{1,3} ?[6-9]\\d{9}$")
    private String customerContactNumber;
}
