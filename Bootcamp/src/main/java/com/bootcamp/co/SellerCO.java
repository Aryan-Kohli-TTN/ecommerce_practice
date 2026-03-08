package com.bootcamp.co;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SellerCO {
    @NotBlank(message="{validations.email.required}")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{validations.email.pattern}")
    @Size(max = 255, message = "{validations.email.size}")
    private String email;

    @NotBlank(message = "{validations.firstName.required}")
    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.firstName.pattern}")
    private String firstName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.middleName.pattern}")
    @Size(min = 3, max = 50, message = "{validations.middleName.size}")
    private String middleName;

    @NotBlank(message = "{validations.lastName.required}")
    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.lastName.pattern}")
    private String lastName;

    @NotBlank(message = "{validations.password.required}")
    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;

    @NotBlank(message = "{validations.confirmPassword.required}")
    @Pattern(message = "{validations.confirmPassword.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String confirmPassword;

    @NotBlank(message = "{validations.gstNo.required}")
    @Pattern(message = "{validations.gstNo.pattern}",
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9]{1}Z[a-zA-Z0-9]{1}$")
    private String gstNo;

    @NotBlank(message = "{validations.companyName.required}")
    @Pattern(
            regexp = "^[A-Za-z0-9][A-Za-z0-9 .-]{2,24}$",
            message = "{validations.companyName.pattern}"
    )
    @Size(min = 3, max = 25,message = "{validations.companyName.size}")
    private String companyName;

    @NotBlank(message = "{validations.companyContactNumber.required}")
    @Pattern(message = "{validations.companyContactNumber.pattern}", regexp = "^\\+[0-9]{1,3} ?[6-9]\\d{9}$")
    private String companyContactNumber;

    @Valid
    @NotNull(message = "{validations.address.required}")
    private AddressCO address;
}
