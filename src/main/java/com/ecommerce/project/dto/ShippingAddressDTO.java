package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDTO {
    private Long shippingAddressId;
    private String name;
    private String buildingName;
    private String locality;
    private String city;
    private String state;
    private String zipcode;
    private String mobileNumber;
}
