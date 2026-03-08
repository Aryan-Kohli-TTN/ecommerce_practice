package com.bootcamp.service;

import com.bootcamp.co.CommonProfileUpdatePatchCO;
import com.bootcamp.co.CustomerProfileUpdateCO;
import com.bootcamp.co.SellerProfileUpdateCO;
import com.bootcamp.response.ApiResponse;

public interface ProfileService {
    ApiResponse<Object> getProfile();

    ApiResponse<Object> updateProfile(CommonProfileUpdatePatchCO commonProfileUpdatePatchCO);

    ApiResponse<Object> updateProfileCustomer(CustomerProfileUpdateCO customerProfileUpdateCO);

    ApiResponse<Object> updateProfileSeller(SellerProfileUpdateCO sellerProfileUpdateCO);
}
