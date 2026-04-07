package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.model.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface AddressService {
    AddressDTO createAddress(@Valid AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddress(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    List<AddressDTO> updateAddress(AddressDTO addressDTO, Long addressId);

    List<AddressDTO> deleteAddress(Long addressId);

    Map<String, Object> reverseGeocode(double lat, double lng);
}
