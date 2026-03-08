package com.bootcamp.co;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryFieldValuesCO {
    UUID fieldId;
    @Valid
    List<
            @Pattern(
                    regexp = "^(?i)[a-z0-9\\s.\\-xX]{1,50}$",
                    message = "Only letters, numbers, spaces, dots (.), hyphens (-), and 'x' or 'X' are allowed. Max 50 characters.")
                    String
            > values;
}
