package com.bootcamp.service;

import com.bootcamp.co.SellerCO;
import com.bootcamp.response.ApiResponse;

public interface SellerService {
    ApiResponse<Object> saveSeller(SellerCO sellerCO);
}
