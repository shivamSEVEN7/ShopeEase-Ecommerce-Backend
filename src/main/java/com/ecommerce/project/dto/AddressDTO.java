package com.ecommerce.project.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String buildingName;
    private String locality;
    private String landmark;
    private String city;
    private String state;
    private String zipcode;
    private String mobileNumber;
}
