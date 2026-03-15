package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.utility.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;
    @Autowired
    AuthUtil authUtil;
    @PostMapping("/address")
    ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        AddressDTO savedAddressDTO =  addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);

    }
    @GetMapping("/address")
    ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> allAddresses =  addressService.getAllAddresses();
        return new ResponseEntity<>(allAddresses, HttpStatus.OK);

    }
    @GetMapping("/address/{addressId}")
    ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId){
        AddressDTO address =  addressService.getAddress(addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }
    @GetMapping("/user/address")
    ResponseEntity<List<AddressDTO>> getUserAddresses(){
        User loggedInUser = authUtil.loggedInUser();
        List<AddressDTO> addresses =  addressService.getUserAddresses(loggedInUser);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
    @PutMapping("/address/{addressId}")
    ResponseEntity<List<AddressDTO>> updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long addressId){
         List<AddressDTO> userAddresses =  addressService.updateAddress(addressDTO, addressId);
        return new ResponseEntity<>(userAddresses, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    ResponseEntity<List<AddressDTO>> deleteAddress(@PathVariable Long addressId){
        List<AddressDTO> addressDTOS =  addressService.deleteAddress(addressId);
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

}
