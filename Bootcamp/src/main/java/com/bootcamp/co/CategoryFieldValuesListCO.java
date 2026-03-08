package com.bootcamp.co;

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
public class CategoryFieldValuesListCO {
    UUID categoryId;
    @Valid
    List<CategoryFieldValuesCO> valuesList;
}
