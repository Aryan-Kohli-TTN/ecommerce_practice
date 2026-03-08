package com.bootcamp.service;

import com.bootcamp.co.AddressCO;
import com.bootcamp.co.AddressPatchCO;
import com.bootcamp.co.AddressPutCO;
import com.bootcamp.entity.user.Address;
import com.bootcamp.entity.user.User;
import com.bootcamp.response.ApiResponse;

import java.util.UUID;

public interface AddressService {
    ApiResponse<Object> getAddress();

    ApiResponse<Object> saveAddress(AddressCO addressCO);

    Address getDefaultAddress(User user);

    ApiResponse<Object> deleteAddress(String address_id);

    ApiResponse<Object> updatePatchAddress(String addressId, AddressPatchCO addressPatchCO);

    ApiResponse<Object> updatePutAddress(String addressId, AddressPutCO addressPutCO);

    ApiResponse<Object> setDefaultAddress(String addressId);
}
