package com.bootcamp.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CategoryFieldValueDTO {
    UUID fieldId;
    String values;
}
