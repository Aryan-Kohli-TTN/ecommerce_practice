package com.bootcamp.response;

import com.bootcamp.dto.CustomerProfileDTO;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseUtil {
    public static<T> ApiResponse<Object> ok(String message){
        return new ApiResponse<>("OK", "SUCCESS",new MessageResponse(message));
    }
    public static<T> ApiResponse<Object> okWithData(T data){
        return new ApiResponse<>("OK", "SUCCESS",data);
    }
    public static<T> ApiResponse<Object> withStatus(HttpStatus http,T data){
        return new ApiResponse<>(http.name(), "SUCCESS", data);
    }
    public static<T> ApiResponse<Object> errorStatus(HttpStatus http,String message){
        return new ApiResponse<>(http.name(), message, null);
    }

}
