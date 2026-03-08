package com.bootcamp.service;

import com.bootcamp.co.AuthRequest;
import com.bootcamp.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    ApiResponse<Object> userLogin(AuthRequest authRequest, HttpServletResponse response);

    ApiResponse<Object> refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    ApiResponse<Object> logoutUser(String refreshToken, HttpServletResponse response, HttpServletRequest request);
}
