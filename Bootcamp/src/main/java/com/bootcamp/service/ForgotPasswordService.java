package com.bootcamp.service;

import com.bootcamp.co.ForgotPasswordCO;
import com.bootcamp.response.ApiResponse;

public interface ForgotPasswordService {
    ApiResponse<Object> forgotPasswordMailSend(String email);

    ApiResponse<Object> updateForgotPassword(ForgotPasswordCO forgotPasswordCO);
}
