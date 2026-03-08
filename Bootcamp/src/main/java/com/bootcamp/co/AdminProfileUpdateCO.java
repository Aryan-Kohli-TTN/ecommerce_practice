package com.bootcamp.co;


import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AdminProfileUpdateCO {

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.firstName.pattern}")
    private String firstName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.middleName.pattern}")
    private String middleName;

    @Pattern(regexp = "^[A-Z][a-z]{2,49}$", message = "{validations.lastName.pattern}")
    private String lastName;

    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;
}
