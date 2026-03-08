package com.bootcamp.co;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressPutCO {
    @NotBlank(message = "{validations.city.required}")
    @Pattern(regexp = "^[A-Z][a-z]{1,50}$", message = "{validations.city.pattern}")
    private String city;

    @NotBlank(message = "{validations.state.required}")
    @Pattern(regexp = "^[A-Za-z ]{3,50}$", message = "{validations.state.pattern}")
    private String state;

    @NotBlank(message = "{validations.country.required}")
    @Pattern(regexp = "^[A-Za-z ]{3,50}$", message = "{validations.country.pattern}")
    private String country;

    @NotBlank(message = "{validations.address.line.required}")
    @Size(min = 3, max = 120, message = "{validations.address.line.pattern}")
    private String addressLine;

    @NotBlank(message = "{validations.zipcode.required}")
    @Pattern(regexp = "^[A-Za-z0-9\\- ]{3,10}$", message = "{validations.zipcode.pattern}")
    private String zipCode;

    @NotBlank(message = "{validations.label.required}")
    @Pattern(regexp = "^[A-Za-z ]{3,20}$", message = "{validations.label.pattern}")
    private String label;




    @Override
    public String toString() {
        return "Address{" +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
