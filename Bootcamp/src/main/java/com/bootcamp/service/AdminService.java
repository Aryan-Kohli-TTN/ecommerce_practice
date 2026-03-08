package com.bootcamp.service;

import com.bootcamp.co.CustomerCO;
import com.bootcamp.co.SellerCO;
import com.bootcamp.response.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    ApiResponse<Object> activateUser(String user_id);

    ApiResponse<Object> deactivateUser(String user_id);

    ApiResponse<Object> getUserWithAddress(String user_id);

    ApiResponse<Object> getUserWithoutAddress(String user_id);

    ApiResponse<Object> getAddressWithoutUser(String address_id);

    ApiResponse<Object> getAddressWithUser(String address_id);

    ApiResponse<Object> getAllCustomers(String Size, String Offset, String orderBy, String sortBy, String email, boolean withAddress);

    ApiResponse<Object> getAllSellers(String Size, String Offset, String orderBy, String sortBy, String email);

    ApiResponse<Object> addManyCustomers(List<CustomerCO> customers);

    ApiResponse<Object> addManySellers(List<SellerCO> sellers);
}
