package com.bootcamp.co;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordCO {
    @NotBlank(message = "Forgot password Token is required")
    String forgotPasswordToken;

    @NotBlank(message = "{validations.password.required}")
    @Pattern(message = "{validations.password.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String password;

    @NotBlank(message = "{validations.confirmPassword.required}")
    @Pattern(message = "{validations.confirmPassword.pattern}",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,15}$")
    private String confirmPassword;

}
