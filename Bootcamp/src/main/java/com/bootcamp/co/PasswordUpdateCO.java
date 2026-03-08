package com.bootcamp.co;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateCO {
    @NotBlank(message = "{validations.password.required}")
    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;

    @NotBlank(message = "{validations.confirmPassword.required}")
    @Pattern(message = "{validations.confirmPassword.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String confirmPassword;
}
