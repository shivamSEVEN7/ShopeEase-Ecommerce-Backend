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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthUtil authUtil;
    @Value("${tomtom.api.key}")
    private String tomtomApiKey;
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
        oldAddress.setLatitude(addressDTO.getLatitude());
        oldAddress.setLongitude(addressDTO.getLongitude());
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

    @Override
    public Map<String, Object> reverseGeocode(double lat, double lng) {

        String url = "https://api.tomtom.com/search/2/reverseGeocode/"
                + lat + "," + lng
                + ".json?key=" + tomtomApiKey;

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        Map address = (Map) ((Map)((java.util.List)response.get("addresses")).get(0)).get("address");

        String street = address.get("streetName") != null
                ? address.get("streetName").toString()
                : address.get("street") != null
                ? address.get("street").toString()
                : "";

        String area = address.get("municipalitySubdivision") != null
                ? address.get("municipalitySubdivision").toString()
                : "";

        String streetAddress = (!street.isEmpty() && !area.isEmpty())
                ? street + ", " + area
                : (!street.isEmpty() ? street : area);

        String city = address.get("municipality") != null
                ? address.get("municipality").toString()
                : address.get("localName") != null
                ? address.get("localName").toString()
                : "";

        String state = address.get("countrySubdivision") != null
                ? address.get("countrySubdivision").toString()
                : "";

        String pinCode = address.get("postalCode") != null
                ? address.get("postalCode").toString()
                : "";

        Map<String, Object> result = new HashMap<>();
        result.put("streetAddress", streetAddress);
        result.put("city", city);
        result.put("state", state);
        result.put("pinCode", pinCode);

        return result;
    }

}
 