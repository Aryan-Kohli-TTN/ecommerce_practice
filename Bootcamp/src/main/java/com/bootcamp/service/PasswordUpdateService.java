package com.bootcamp.service;

import com.bootcamp.co.PasswordUpdateCO;
import com.bootcamp.response.ApiResponse;

public interface PasswordUpdateService {
    ApiResponse<Object> updatePassword(PasswordUpdateCO passwordUpdateCO);
}
