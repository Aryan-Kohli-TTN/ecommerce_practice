package com.bootcamp.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
}
