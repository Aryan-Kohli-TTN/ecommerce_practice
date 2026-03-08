package com.bootcamp.service;

import com.bootcamp.co.CustomerCO;
import com.bootcamp.response.ApiResponse;

public interface CustomerService {
    ApiResponse<Object> saveCustomer(CustomerCO customerCO);

    ApiResponse<Object> sendNewActivationMail(String email);

    ApiResponse<Object> activate_customer(String token);
}
