package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.AddressRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthUtil authUtil;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address savedAddress = modelMapper.map(addressDTO, Address.class);
        User loggedInUser = authUtil.loggedInUser();
        if(loggedInUser==null){
            throw new APIException("You need to be logged in to do this");
        }
        savedAddress.setUser(loggedInUser);
        return modelMapper.map(addressRepo.save(savedAddress), AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> allAddresses = addressRepo.findAll();
        List<AddressDTO> allAddressesDto = allAddresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return allAddressesDto;
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address =  addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", addressId, "address id"));
        if(address.getUser().getUserId()!= authUtil.loggedInUserId() && !authUtil.hasRole("ADMIN")){
            throw new AccessDeniedException("Not Authorized");
        }
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOS = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public List<AddressDTO> updateAddress(AddressDTO addressDTO, Long addressId) {
        Address oldAddress = addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", addressId, "address id"));
        if(oldAddress.getUser().getUserId()!= authUtil.loggedInUserId() && !authUtil.hasRole("ADMIN")){
            throw new AccessDeniedException("Not Authorized");
        }
        oldAddress.setName(addressDTO.getName());
        oldAddress.setBuildingName(addressDTO.getBuildingName());
        oldAddress.setCity(addressDTO.getCity());
        oldAddress.setState(addressDTO.getState());
        oldAddress.setLocality(addressDTO.getLocality());
        oldAddress.setLandmark(addressDTO.getLandmark());
        oldAddress.setZipcode(addressDTO.getZipcode());
        oldAddress.setMobileNumber(addressDTO.getMobileNumber());
        Address updatedAddress = addressRepo.save(oldAddress);
        return getUserAddresses(authUtil.loggedInUser());
    }

    @Override
    public List<AddressDTO> deleteAddress(Long addressId) {
        Address address = addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", addressId, "address id"));
        User user = address.getUser();
        if(address.getUser().getUserId()!= authUtil.loggedInUserId() && !authUtil.hasRole("ADMIN")){
            throw new AccessDeniedException("Not Authorized");
        }
        addressRepo.deleteById(addressId);

        return user.getAddresses().stream().map(address1 -> modelMapper.map(address1, AddressDTO.class) ).toList();
    }

}
 